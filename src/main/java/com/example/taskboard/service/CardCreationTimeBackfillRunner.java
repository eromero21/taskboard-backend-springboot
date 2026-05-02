package com.example.taskboard.service;

import com.example.taskboard.repository.CardRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class CardCreationTimeBackfillRunner implements ApplicationRunner {
    private final CardRepository cardRepository;

    public CardCreationTimeBackfillRunner(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        cardRepository.backfillCreatedAt(Instant.now());
    }
}
