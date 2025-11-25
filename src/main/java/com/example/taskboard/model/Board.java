package com.example.taskboard.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private String id;
    private String name;
    private List<Column> columns;

    public Board() {
        this.id = "";
        this.name = "";
        this.columns = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void addCardToColumn(int index, Card card) {
        this.columns.get(index).addCard(card);
    }

    public void moveCard(int from, int to, Card card) {
        this.columns.get(from).removeCard(card);
        this.columns.get(to).addCard(card);
    }

    public void removeCardFromColumn(int index, Card card) {
        this.columns.get(index).removeCard(card);
    }
}
