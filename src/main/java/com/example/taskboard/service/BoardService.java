package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.ColumnEntity;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.repository.CardRepository;
import com.example.taskboard.repository.ColumnRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BoardService {
    private final String[] columnNames = {"Backlog", "Todo", "In Progress", "Completed"};
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;

    public BoardService(CardRepository cardRepository, BoardRepository boardRepository, ColumnRepository columnRepository) {
        this.cardRepository = cardRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
    }

    public Board createBoard(String name) {
        Board board = new Board();
        board.setName(name);

        for (String columnName : columnNames) {
            ColumnType type = ColumnType.valueOf(columnName.toUpperCase().replace(" ", "_"));
            ColumnEntity column = new ColumnEntity(columnName, type);
            board.addColumn(column);
        }

        return boardRepository.save(board);
    }

    public Board getBoard(Long boardId) {
        return boardRepository.findByIdWithColumnsAndCards(boardId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    public Card createCard(Long boardId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));

        ColumnEntity backlog = columnRepository.findByBoardIdAndType(boardId, ColumnType.BACKLOG).orElseThrow(
                () -> new IllegalArgumentException("Backlog column not found"));

        Card newCard = new Card(board, backlog, title, description);

        backlog.getCards().add(newCard);

        return cardRepository.save(newCard);
    }

    public Card moveCard(Long boardId, Long cardId, ColumnType columnId) {
        if (columnId == null) {
            throw new IllegalArgumentException("Invalid column type.");
        }

        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        if (!theCard.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Card does not belong to this board.");
        }

        ColumnEntity targetColumn = columnRepository.findByBoardIdAndType(boardId, columnId).orElseThrow(
                () -> new IllegalArgumentException("Column not belong to this board.")
        );

        theCard.setColumn(targetColumn);
        return cardRepository.save(theCard);
    }

    public void deleteCard(Long boardId, Long cardId) {
        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!theCard.getBoard().getId().equals(boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        cardRepository.delete(theCard);
    }

    public Card editCard(Long boardId, Long cardId, Card newCard) {
        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        if (!theCard.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Card does not belong to this board.");
        }

        theCard.setTitle(newCard.getTitle());
        theCard.setDescription(newCard.getDescription());

        return cardRepository.save(theCard);
    }

    public boolean hasCard(Long cardId) {
        return cardRepository.existsById(cardId);
    }
}
