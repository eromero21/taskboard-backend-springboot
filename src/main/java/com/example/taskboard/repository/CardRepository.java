package com.example.taskboard.repository;

import com.example.taskboard.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByBoardId(Long boardId);
}
