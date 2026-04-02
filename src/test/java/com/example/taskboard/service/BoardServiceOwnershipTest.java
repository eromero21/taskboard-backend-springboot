package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnEntity;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.model.User;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.repository.CardRepository;
import com.example.taskboard.repository.ColumnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceOwnershipTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BoardService boardService;

    @Test
    void createBoard_setsOwnerAndColumns() {
        User owner = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(owner, "id", 5L);

        when(userService.getUserById(5L)).thenReturn(owner);
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Board board = boardService.createBoard(5L, "Project Board");

        assertEquals("Project Board", board.getName());
        assertSame(owner, board.getOwner());
        assertEquals(4, board.getColumns().size());
        verify(userService).getUserById(5L);
        verify(boardRepository).save(board);
    }

    @Test
    void getAllBoards_returnsOnlyOwnedBoards() {
        Board board = new Board("Project Board");

        when(boardRepository.findAllByOwnerId(5L)).thenReturn(List.of(board));

        List<Board> result = boardService.getAllBoards(5L);

        assertEquals(1, result.size());
        assertSame(board, result.getFirst());
        verify(boardRepository).findAllByOwnerId(5L);
    }

    @Test
    void getBoard_rejectsBoardOutsideOwnerScope() {
        when(boardRepository.findByIdWithColumnsAndCards(20L, 5L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                boardService.getBoard(5L, 20L));

        assertEquals("Board not found", exception.getMessage());
    }

    @Test
    void editCard_rejectsCardOutsideOwnerScope() {
        Card updated = new Card();
        updated.setTitle("Updated");
        updated.setDescription("Updated description");

        when(cardRepository.findByIdAndBoardIdAndBoardOwnerId(8L, 20L, 5L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                boardService.editCard(5L, 20L, 8L, updated));

        assertEquals("Card ID doesn't exist..", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void moveCard_usesOwnerScopedCardAndColumnLookups() {
        Board board = new Board("Project Board");
        ReflectionTestUtils.setField(board, "id", 20L);

        ColumnEntity currentColumn = new ColumnEntity("Backlog", ColumnType.BACKLOG);
        currentColumn.setBoard(board);

        ColumnEntity targetColumn = new ColumnEntity("Todo", ColumnType.TODO);
        targetColumn.setBoard(board);

        Card card = new Card(board, currentColumn, "Title", "Description");
        ReflectionTestUtils.setField(card, "id", 8L);

        when(cardRepository.findByIdAndBoardIdAndBoardOwnerId(8L, 20L, 5L)).thenReturn(Optional.of(card));
        when(columnRepository.findByBoardIdAndBoardOwnerIdAndType(20L, 5L, ColumnType.TODO))
                .thenReturn(Optional.of(targetColumn));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = boardService.moveCard(5L, 20L, 8L, ColumnType.TODO);

        assertSame(targetColumn, result.getColumn());
        verify(cardRepository).findByIdAndBoardIdAndBoardOwnerId(8L, 20L, 5L);
        verify(columnRepository).findByBoardIdAndBoardOwnerIdAndType(20L, 5L, ColumnType.TODO);
    }
}
