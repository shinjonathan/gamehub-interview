package com.shin.leaderboard.score;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
class ScoreServiceTest {

    @Autowired
    ScoreService scoreService;

    @Autowired
    ScoreRepository scoreRepository;

    private final UUID game1Id = UUID.fromString("55364F28-3670-451D-9BD4-ADC265769B0A");
    private final UUID game2Id = UUID.fromString("32EA6860-7D17-4792-874F-081D35ED004C");
    private final UUID gamerId = UUID.fromString("AC598975-31EB-429C-864B-443899EAC48B");

    @Test
    void getGamesPlayed() {
        StepVerifier.create(scoreService.getGamesPlayed(gamerId))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getScores() {
        StepVerifier.create(scoreService.getScores(gamerId, game1Id))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void addScore() {
        Score score = scoreService.addScore(new Score(null, game2Id, gamerId, 1)).block();

        StepVerifier.create(scoreService.getScores(gamerId, game2Id))
                .expectNextCount(2)
                .then(() -> {scoreRepository.deleteById(score.getId());})
                .verifyComplete();


    }

    @Test
    void getGamesByPopularity() {
        StepVerifier.create(scoreService.getGamesByPopularity())
                .expectNextMatches(t -> t.getId().equals(game1Id))
                .expectNextMatches(t -> t.getId().equals(game2Id))
                .verifyComplete();
    }

    @Test
    void getGamersForGame() {
        StepVerifier.create(scoreService.getGamersForGame(game1Id))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getGamersForGame_verifyOrder() {
        StepVerifier.create(scoreService.getGamersForGame(game1Id))
                .expectNextMatches(t -> t.getScore() == 5)
                .expectNextMatches(t -> t.getScore() == 2)
                .verifyComplete();
    }


    @Test
    void getAverageScoreForGame() {
        StepVerifier.create(scoreService.getAverageScoreForGame(game1Id))
                .expectNextMatches(r -> r.getScore() == 3)
                .verifyComplete();

    }
}