package com.example.taskboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ColumnEntity> columns = new LinkedHashSet<>();

    public Board() {}

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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setColumns(Set<ColumnEntity> columns) {
        this.columns = columns;
    }

    public Set<ColumnEntity> getColumns() {
        return columns;
    }

    public ColumnEntity getColumnById(ColumnType columnId) {
        for (ColumnEntity column : columns) {
            if (column.getType().equals(columnId)) {
                return column;
            }
        }
        return null;
    }

    public void addColumn(ColumnEntity column) {
        if (column == null) {return;}
        columns.add(column);
        column.setBoard(this);
    }

    public void addCardToColumn(ColumnType colId, Card card) {
        ColumnEntity column = getColumnById(colId);
        if (column == null) {
            return;
        }
        column.addCard(card);
    }

    public void moveCard(ColumnType from, ColumnType to, Card card) {
        removeCardFromColumn(from, card);
        addCardToColumn(to, card);
    }

    public void removeCardFromColumn(ColumnType colId, Card card) {
        ColumnEntity column = getColumnById(colId);
        if (column == null) {
            return;
        }
        column.removeCard(card);
    }

}
