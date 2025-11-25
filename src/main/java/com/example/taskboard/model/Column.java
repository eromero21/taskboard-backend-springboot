package com.example.taskboard.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private ColumnType id;
    private String name;
    private List<Card> cards;

    public Column(String name, ColumnType id) {
        this.name = name;
        this.id = id;
        this.cards = new ArrayList<>();
    }

    public ColumnType getId() {
        return id;
    }

    public void setId(ColumnType id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
