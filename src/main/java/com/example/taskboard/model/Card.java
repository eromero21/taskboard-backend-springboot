package com.example.taskboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @jakarta.persistence.Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    @ManyToOne
    @JoinColumn(name = "column_id")
    @JsonIgnore
    private ColumnEntity column;

    public Card (Board board, ColumnEntity column, String title, String description) {
        this.board = board;
        this.column = column;
        this.title = title;
        this.description = description;
    }

    public Card() {};

    public ColumnType getColumnId() {
        if (column != null) {
            return this.column.getType();
        }
        return null;
    }

    public ColumnEntity getColumn() {
        return this.column;
    }

    public void setColumn(ColumnEntity column) {
        this.column = column;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
