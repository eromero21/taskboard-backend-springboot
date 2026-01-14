package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnEntity;
import com.example.taskboard.model.ColumnType;
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

    @InjectMocks
    private BoardService boardService;

    @Test
    void createCard_success() {
        Long boardId = 1L;
        String title = "My test card";
        String description = "This card is for testing purposes.";

        Board board = new Board();
        board.setId(boardId);

        ColumnEntity backlog = new ColumnEntity();
        backlog.setType(ColumnType.BACKLOG);
        backlog.setCards(new ArrayList<>());

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        when(columnRepository.findByBoardIdAndType(boardId, ColumnType.BACKLOG)).thenReturn(Optional.of(backlog));

        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));


        Card resultCard = boardService.createCard(boardId, title, description);

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
        Long boardId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(boardId, null, "description");
        });

        assertEquals("Title cannot be empty", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_fail_invalidBoardId() {
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(boardId, "title", "description");
        });

        assertEquals("Board not found", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_fail_columnNotFound() {
        Long boardId = 1L;

        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        when(columnRepository.findByBoardIdAndType(boardId, ColumnType.BACKLOG)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createCard(boardId, "title", "description");
        });

        assertEquals("Backlog column not found", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }
}
