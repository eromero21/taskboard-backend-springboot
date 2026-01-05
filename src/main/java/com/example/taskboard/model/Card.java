package com.example.taskboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    private String id;
    private String title;

    @jakarta.persistence.Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ColumnType columnId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    public Card (String id, ColumnType columnId, String title, String description) {
        this.id = id;
        this.columnId = columnId;
        this.title = title;
        this.description = description;
    }

    public Card() {};

    public ColumnType getColumnId() {
        return columnId;
    }

    public void setColumnId(ColumnType columnId) {
        this.columnId = columnId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
