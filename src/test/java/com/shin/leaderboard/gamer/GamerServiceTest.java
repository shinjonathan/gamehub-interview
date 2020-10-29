package com.shin.leaderboard.gamer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
class GamerServiceTest {

    @Autowired
    GamerService gamerService;


    @Test
    void getAll() {
        StepVerifier.create(gamerService.getAll()).expectNextCount(2).verifyComplete();
    }

    @Test
    void getGameById() {
        StepVerifier.create(gamerService.getGamerById(UUID.randomUUID())).expectNextCount(0).verifyComplete();
    }

    @Test
    void createGame() {
        Gamer game = new Gamer(null, "Dead by Daylight", "PC", "BEHAVIOR");

        StepVerifier.create(gamerService.createGamer(game))
                .expectNextMatches(r -> r.getId() != null)
                .verifyComplete();
    }

    @Test
    void updateGame() {
        Gamer game = new Gamer(null, "First Name", "Last Name", "bad@email.com");
        UUID id = gamerService.createGamer(game).block().getId();

        Gamer updatedGamer = new Gamer(id, "First Name", "Last Name", "good@email.com");

        gamerService.updateGamer(updatedGamer).subscribe();

        StepVerifier.create(gamerService.getGamerById(id))
                .expectNext(updatedGamer)
                .verifyComplete();
    }
}