package com.example.taskboard.repository;

import com.example.taskboard.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByBoardId(Long boardId);

    List<Card> findByBoardOwnerId(Long ownerId);

    Optional<Card> findByIdAndBoardIdAndBoardOwnerId(Long cardId, Long boardId, Long ownerId);

    @Modifying
    @Query("update Card c set c.createdAt = :createdAt where c.createdAt is null")
    int backfillCreatedAt(Instant createdAt);
}
