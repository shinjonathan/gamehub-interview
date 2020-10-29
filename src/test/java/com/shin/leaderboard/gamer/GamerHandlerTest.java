package com.shin.leaderboard.gamer;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest
@AutoConfigureRestDocs
@ContextConfiguration(classes = GamerHandler.class)
class GamerHandlerTest {

    @MockBean
    GamerService gamerService;

    @Autowired
    private WebTestClient webClient;

    private final Gamer gamer = new Gamer(UUID.randomUUID(), "First Name", "Last Name", "email@email.com");

    @Test
    void getAllGames() {
        when(gamerService.getAll())
                .thenReturn(Flux
                        .just(gamer)
                );

        webClient.get()
                .uri("/gamers")
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getGamers",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                subsectionWithPath("[]").type(JsonFieldType.ARRAY).description("List of Gamers")
                        ))
        );
    }

    @Test
    void getOneGame() {
        when(gamerService.getGamerById(any()))
                .thenReturn(Mono
                        .just(gamer)
                );

        webClient.get()
                .uri("/gamers/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk().expectBody().consumeWith(
                document("getOneGamer",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseBody(),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("Unique ID of the Gamer"),
                                fieldWithPath("firstName").type(JsonFieldType.STRING).description("First Name"),
                                fieldWithPath("lastName").type(JsonFieldType.STRING).description("Last Name"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email")
                        ))
        );
    }

    @Test
    void createAGame() throws JsonProcessingException {
        when(gamerService.createGamer(any()))
                .thenReturn(Mono.just(gamer.withId(UUID.randomUUID())));


        webClient.post()
                .uri("/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(gamer.withId(null)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(
                        document("createGamer",
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseBody())
                );

        verify(gamerService, times(1)).createGamer(any());

    }

    @Test
    void updateAGame() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        Gamer gamer = new Gamer(null, "First Name", "Last Name", "email@email.com");
        Gamer savedGamer = new EasyRandom().nextObject(Gamer.class).withId(id);


        when(gamerService.getGamerById(any()))
                .thenReturn(Mono.just(savedGamer));

        when(gamerService.updateGamer(any()))
                .then(r -> Mono.just(
                        ((Gamer) r.getArgument(0)).withId(id)
                ));

        webClient.post()
                .uri("/gamers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(gamer))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .consumeWith(
                        document("updateGamer",
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseBody())
                );

        verify(gamerService, times(1)).updateGamer(eq(gamer.withId(id)));

    }
}