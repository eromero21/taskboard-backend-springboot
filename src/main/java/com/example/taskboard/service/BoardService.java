package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Column;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;
import com.example.taskboard.repository.BoardRepository;
import com.example.taskboard.repository.CardRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BoardService {
    private final String[] columnNames = {"Backlog", "Todo", "In Progress", "Completed"};
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;

    public BoardService(CardRepository cardRepository, BoardRepository boardRepository) {
        this.cardRepository = cardRepository;
        this.boardRepository = boardRepository;
    }

    public Board getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));

        Map<ColumnType, Column> columns = new LinkedHashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            ColumnType type = ColumnType.values()[i];
            Column col = new Column(columnNames[i], type);
            columns.put(type, col);
        }

        List<Card> cards = cardRepository.findByBoardId(boardId);
        for (Card card : cards) {
            columns.get(card.getColumnId()).addCard(card);
        }

        board.setColumns(columns);

        return board;
    }

    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    public Card createCard(Long boardId, String title, String description) {
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("Board not found"));

        String newId = UUID.randomUUID().toString();
        Card newCard = new Card(newId, ColumnType.BACKLOG, title, description);
        newCard.setBoard(board);
        Card savedCard = cardRepository.save(newCard);
        board.addCardToColumn(newCard.getColumnId(), savedCard);
        return savedCard;
    }

    public Card moveCard(Long boardId, String cardId, ColumnType columnId) {
        if (!validEnum(columnId)) {
            throw new IllegalArgumentException("Invalid column type.");
        }

        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        if (!theCard.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Card does not belong to this board.");
        }

        theCard.setColumnId(columnId);

        return cardRepository.save(theCard);
    }

    public Card deleteCard(String cardId) {
        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        cardRepository.deleteById(cardId);

        return theCard;
    }

    public Card editCard(String cardId, Card newCard) {
        Card theCard = cardRepository.findById(cardId).orElseThrow(() ->
                new IllegalArgumentException("Card ID doesn't exist.."));

        theCard.setTitle(newCard.getTitle());
        theCard.setDescription(newCard.getDescription());

        return cardRepository.save(theCard);
    }

    public boolean hasCard(String cardId) {
        return cardRepository.existsById(cardId);
    }

    public boolean validEnum(ColumnType columnType) {
        try {
            ColumnType.valueOf(columnType.toString());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
