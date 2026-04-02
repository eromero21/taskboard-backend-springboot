package com.example.taskboard.repository;

import com.example.taskboard.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByBoardId(Long boardId);

    List<Card> findByBoardOwnerId(Long ownerId);

    Optional<Card> findByIdAndBoardIdAndBoardOwnerId(Long cardId, Long boardId, Long ownerId);
}
