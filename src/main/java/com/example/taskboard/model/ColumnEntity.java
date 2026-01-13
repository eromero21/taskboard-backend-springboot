package com.example.taskboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_column")
public class ColumnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ColumnType type;

    private String name;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    public ColumnEntity(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public ColumnEntity() {}

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        if (card == null) {return;}
        this.cards.add(card);
        card.setColumn(this);
    }

    public void removeCard(Card card) {
        if (card == null) {return;}

        this.cards.removeIf(c -> c.getId().equals(card.getId()));

        if (card.getColumn() == this) {
            card.setColumn(null);
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
