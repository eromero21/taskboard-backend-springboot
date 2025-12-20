package com.example.taskboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private String id;
    private String name;
    private Map<ColumnType, Column> columns;

    public Board() {
        this.id = "";
        this.name = "";
        this.columns = new HashMap<>();
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

    public Map<ColumnType, Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        columns.put(column.getId(), column);
    }

    public void addCardToColumn(ColumnType colId, Card card) {
        this.columns.get(colId).addCard(card);
    }

    public void moveCard(ColumnType from, ColumnType to, Card card) {
        this.columns.get(from).removeCard(card);
        this.columns.get(to).addCard(card);
    }

    public void removeCardFromColumn(Card card) {
        this.columns.get(card.getColumnId()).removeCard(card);
    }
}
