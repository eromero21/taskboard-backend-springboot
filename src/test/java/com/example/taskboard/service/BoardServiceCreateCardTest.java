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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceCreateCardTest {

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
    void createCard_success() {
        Long ownerId = 99L;
        Long boardId = 1L;
        String title = "My test card";
        String description = "This card is for testing purposes.";

        Board board = new Board();
        board.setId(boardId);
        board.setOwner(new User("owner@example.test", "hashedPassword"));

        ColumnEntity backlog = new ColumnEntity();
        backlog.setType(ColumnType.BACKLOG);
        backlog.setCards(new ArrayList<>());
        backlog.setBoard(board);

        when(boardRepository.findByIdAndOwnerId(boardId, ownerId)).thenReturn(Optional.of(board));

        when(columnRepository.findByBoardIdAndBoardOwnerIdAndType(boardId, ownerId, ColumnType.BACKLOG))
                .thenReturn(Optional.of(backlog));

        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        Card resultCard = boardService.createCard(ownerId, boardId, title, description);

        assertNotNull(resultCard);
        assertEquals(title, resultCard.getTitle());
        assertEquals(description, resultCard.getDescription());
        assertEquals(board, resultCard.getBoard());
        assertEquals(backlog, resultCard.getColumn());

        verify(cardRepository).save(resultCard);
        assertTrue(backlog.getCards().contains(resultCard));
    }

    @Test
    void createCard_fail_noTitle() {
        Long ownerId = 99L;
        Long boardId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(ownerId, boardId, null, "description");
        });

        assertEquals("Title cannot be empty", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_fail_invalidBoardId() {
        Long ownerId = 99L;
        Long boardId = 1L;

        when(boardRepository.findByIdAndOwnerId(boardId, ownerId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(ownerId, boardId, "title", "description");
        });

        assertEquals("Board not found", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_fail_columnNotFound() {
        Long ownerId = 99L;
        Long boardId = 1L;

        Board board = new Board();
        board.setId(boardId);
        board.setOwner(new User("owner@example.test", "hashedPassword"));

        when(boardRepository.findByIdAndOwnerId(boardId, ownerId)).thenReturn(Optional.of(board));

        when(columnRepository.findByBoardIdAndBoardOwnerIdAndType(boardId, ownerId, ColumnType.BACKLOG))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(ownerId, boardId, "title", "description");
        });

        assertEquals("Backlog column not found", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }
}
