package com.shin.leaderboard.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
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
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest
@AutoConfigureRestDocs
@ContextConfiguration(classes = GameHandler.class)
class GameHandlerTest {

    @MockBean
    GameService gameService;

    @Autowired
    private WebTestClient webClient;

    private final Game game = new Game(UUID.randomUUID(), "Some Game Name", "Some Platform", "Creator");

    @Test
    void getAllGames() {
        when(gameService.getAll())
                .thenReturn(Flux
                        .just(game)
                );

        webClient.get()
                .uri("/games")
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getGames",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of Games")
                        ))
        );
    }

    @Test
    void getOneGame() {
        when(gameService.getGameById(any()))
                .thenReturn(Mono
                        .just(game)
                );

        webClient.get()
                .uri("/games/{id}", game.getId())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getOneGame",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("Unique ID of the Game"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("Game title"),
                                fieldWithPath("platform").type(JsonFieldType.STRING).description("Platform"),
                                fieldWithPath("creator").type(JsonFieldType.STRING).description("Creator")
                        ))
        );
    }

    @Test
    void createAGame() throws JsonProcessingException {
        when(gameService.createGame(any()))
                .thenReturn(Mono.just(game));


        webClient.post()
                .uri("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(game))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(
                document("createGame",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody())
        );

        verify(gameService, times(1)).createGame(any());

    }

    @Test
    void updateAGame() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        Game game = new Game(null, "Game Name", "Game Platform", "Creator");
        Game savedGame = new EasyRandom().nextObject(Game.class).withId(id);


        when(gameService.getGameById(any()))
                .thenReturn(Mono.just(savedGame));

        when(gameService.updateGame(any()))
                .then(r -> Mono.just(
                        ((Game) r.getArgument(0)).withId(id)
                ));

        webClient.post()
                .uri("/games/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(game))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .consumeWith(
                        document("updateGame",
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseBody())
                );

        verify(gameService, times(1)).updateGame(eq(game.withId(id)));

    }
}