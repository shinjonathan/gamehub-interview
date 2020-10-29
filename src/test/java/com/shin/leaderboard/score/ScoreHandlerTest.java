package com.shin.leaderboard.score;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shin.leaderboard.game.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest
@AutoConfigureRestDocs
@ContextConfiguration(classes = ScoreHandler.class)
class ScoreHandlerTest {

    @MockBean
    ScoreService scoreService;

    @Autowired
    private WebTestClient webClient;

    private final Score score = new Score(1L, UUID.randomUUID(), UUID.randomUUID(), 3);

    @Test
    void getGamesPlayed() {
        when(scoreService.getGamesPlayed(any()))
                .thenReturn(Flux.just(new Game(UUID.randomUUID(), "Some Game", "Some Platform", "Some Creator"))
                );

        webClient.get()
                .uri("/gamers/{id}/games", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getGamesPlayed",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of Games Played")
                        ))
        );
    }

    @Test
    void getScores() {
        when(scoreService.getScores(any(), any()))
                .thenReturn(Flux.just(score));

        webClient.get()
                .uri("/gamers/{gamerId}/games/{gameId}", UUID.randomUUID(), UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getScores",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of gamer's scores for a game")
                        ))
        );

    }

    @Test
    void createScore() throws JsonProcessingException {
        when(scoreService.addScore(any()))
                .then(t -> Mono.just(t.getArgument(0)));

        webClient.post()
                .uri("/gamers/{gamerId}/games/{gameId}", UUID.randomUUID(), UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(new Score(null, null, null, 1)))
                .exchange()
                .expectStatus().isAccepted().expectBody().consumeWith(
                document("createScore",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("gameId").type(JsonFieldType.STRING).description("Id of the Gamer"),
                                subsectionWithPath("gamerId").type(JsonFieldType.STRING).description("Id of the Game"),
                                subsectionWithPath("score").type(JsonFieldType.NUMBER).description("Score. Ranges from 1-5")
                        ))
        );
    }


    @Test
    void createScore_invalidScore_over5() throws JsonProcessingException {
        webClient.post()
                .uri("/gamers/{gamerId}/games/{gameId}", UUID.randomUUID(), UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(new Score(null, null, null, 6)))
                .exchange()
                .expectStatus().isBadRequest().expectBody();
    }

    @Test
    void createScore_invalidScore() throws JsonProcessingException {
        webClient.post()
                .uri("/gamers/{gamerId}/games/{gameId}", UUID.randomUUID(), UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(new Score(null, null, null, -1)))
                .exchange()
                .expectStatus().isBadRequest().expectBody();
    }


    @Test
    void gamersForGame() {
        when(scoreService.getGamersForGame(any()))
                .thenReturn(
                        Flux.just(score)
                );

        webClient.get()
                .uri("/games/{gameId}/gamers", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBodyList(Score.class).consumeWith(
                document("getGamerScoresForGame",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of scores for game (Sorted desc). First in array will be top score")
                        ))
        );
    }


    @Test
    void averageScoreForGame() {
        when(scoreService.getAverageScoreForGame(any()))
                .thenReturn(Mono.just(new Score(null, UUID.randomUUID(), null, 3)));

        webClient.get()
                .uri("/games/{gameId}/metrics", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getGameMetrics",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("gameId").type(JsonFieldType.STRING).description("Id of the Gamer"),
                                subsectionWithPath("score").type(JsonFieldType.NUMBER).description("Average Score. Ranges from 1-5")
                        ))
        );
    }

    @Test
    void getMostPopular() {
        when(scoreService.getGamesByPopularity())
                .thenReturn(
                        Flux.just(new Game(UUID.randomUUID(), "Some Game", "Some Platform", "Some Creator"))
                );

        webClient.get()
                .uri("/popular", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getMostPopular",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of scores for game")
                        ))
        );
    }
}