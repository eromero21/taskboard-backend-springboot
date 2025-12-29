package com.example.taskboard.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Transient
    private Map<ColumnType, Column> columns;

    public Board() {
    }

    public Board(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumns(Map<ColumnType, Column> columns) {
        this.columns = columns;
    }

    public Map<ColumnType, Column> getColumns() {
        return columns;
    }

    public Column getColumnById(ColumnType columnId) {
        return columns.get(columnId);
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
