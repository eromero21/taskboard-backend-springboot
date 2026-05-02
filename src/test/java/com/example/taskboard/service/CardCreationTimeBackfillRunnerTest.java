package com.example.taskboard.service;

import com.example.taskboard.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CardCreationTimeBackfillRunnerTest {

    @Mock
    private CardRepository cardRepository;

    @Test
    void run_backfillsMissingCardCreationTimes() throws Exception {
        CardCreationTimeBackfillRunner runner = new CardCreationTimeBackfillRunner(cardRepository);

        runner.run(null);

        verify(cardRepository).backfillCreatedAt(any());
    }
}
