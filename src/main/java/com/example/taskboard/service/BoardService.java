package com.example.taskboard.service;

import com.example.taskboard.model.Board;
import com.example.taskboard.model.Column;
import com.example.taskboard.model.Card;
import com.example.taskboard.model.ColumnType;

import java.util.*;

public class BoardService {
    Board board = new Board();
    String[] columnNames = {"Backlog", "Todo", "In Progress", "Completed"};
    Map<String, Card> cards = new HashMap<>();

    public BoardService() {
        for (int i = 0; i < columnNames.length; i++) {
            Column newColumn = new Column(columnNames[i], i);
            board.addColumn(newColumn);
        }

        board.setName("My Board");
        board.setId("Numba 1");
    }

    public Board getBoard() {
        return board;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards.values());
    }

    public Card createCard(String title, String description) {
        String newId = UUID.randomUUID().toString();
        Card newCard = new Card(newId, ColumnType.BACKLOG, title, description);
        cards.put(newId, newCard);
        return newCard;
    }

    public Card moveCard(String cardId, ColumnType columnId) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }

        Card theCard = cards.get(cardId);
        theCard.setColumnId(columnId);
        return new Card();
    }

    public Card deleteCard(String cardId) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }

        Card theCard = cards.get(cardId);
        cards.remove(cardId);
        return theCard;
    }

    public Card editCard(String cardId, Card newCard) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }

        Card theCard = cards.get(cardId);
        theCard.setColumnId(newCard.getColumnId());
        theCard.setTitle(newCard.getTitle());
        theCard.setDescription(newCard.getDescription());
        return theCard;
    }
}
