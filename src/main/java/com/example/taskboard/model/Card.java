package com.example.taskboard.model;

public class Card {
    private String id;
    private String title;
    private String description;
    private ColumnType columnId;

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
}
