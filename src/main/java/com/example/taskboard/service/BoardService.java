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
            Column newColumn = new Column(columnNames[i], ColumnType.values()[i]);
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
        board.addCardToColumn(newCard.getColumnId(), newCard);
        return newCard;
    }

    public Card moveCard(String cardId, ColumnType columnId) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }
        if (!validEnum(columnId)) {
            throw new IllegalArgumentException("Invalid column type.");
        }

        Card theCard = cards.get(cardId);
        board.moveCard(theCard.getColumnId(), columnId, theCard);
        theCard.setColumnId(columnId);
        return new Card();
    }

    public Card deleteCard(String cardId) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }

        Card theCard = cards.get(cardId);
        cards.remove(cardId);
        board.removeCardFromColumn(theCard);
        return theCard;
    }

    public Card editCard(String cardId, Card newCard) {
        if (!cards.containsKey(cardId)) {
            throw new IllegalArgumentException("Card ID does not exist.");
        }

        Card theCard = cards.get(cardId);
        theCard.setTitle(newCard.getTitle());
        theCard.setDescription(newCard.getDescription());
        return theCard;
    }

    public boolean hasCard(String cardId) {
        return cards.containsKey(cardId);
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
