package com.shin.leaderboard.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService gameService;

    @Autowired
    GameRepository gameRepository;

    UUID gameId = UUID.fromString("32EA6860-7D17-4792-874F-081D35ED004C");


    @Test
    void getAll() {
        StepVerifier.create(gameService.getAll()).expectNextCount(2).verifyComplete();
    }

    @Test
    void getGameById() {
        StepVerifier.create(gameService.getGameById(gameId)).expectNextCount(1).verifyComplete();
    }

    @Test
    void createGame() {
        Game game = gameService.createGame(new Game(null, "Left 4 Dead", "PC", "VALVE")).block();

        StepVerifier.create(gameService.getAll())
                .expectNextCount(3)
                .then(() -> gameRepository.deleteById(game.getId()))
                .verifyComplete();
    }

    @Test
    void updateGame() {
        Game updatedGame = new Game(gameId, "CS", "PC", "BEHAVIOR");

        gameService.updateGame(updatedGame).subscribe();

        StepVerifier.create(gameService.getGameById(gameId))
                .expectNext(updatedGame)
                .verifyComplete();

        StepVerifier.create(gameService.getAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}