package com.shin.leaderboard.game;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
public class GameHandler {

    @Bean
    RouterFunction<?> gameRoutes(GameRequestHandler requestHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/games"), requestHandler::getGames)
                .andRoute(RequestPredicates.GET("/games/{id}"), requestHandler::getGameById)
                .andRoute(RequestPredicates.POST("/games"), requestHandler::createGame)
                .andRoute(RequestPredicates.POST("/games/{id}"), requestHandler::updateGame);
    }

    @Component
    @RequiredArgsConstructor
    public static class GameRequestHandler {

        private final GameService gameService;

        public Mono<ServerResponse> getGames(ServerRequest request) {
            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(gameService.getAll().collectList(), Game.class);
        }

        public Mono<ServerResponse> getGameById(ServerRequest request) {
            UUID id = UUID.fromString(request.pathVariable("id"));

            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(gameService.getGameById(id), Game.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        }

        public Mono<ServerResponse> createGame(ServerRequest request) {
            return request.bodyToMono(Game.class)
                    .flatMap(gameService::createGame)
                    .flatMap(r ->
                            ServerResponse
                                    .created(UriComponentsBuilder.fromPath("/games/id}").build(r.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(Mono.just(r), Game.class));
        }

        public Mono<ServerResponse> updateGame(ServerRequest request) {
            UUID id = UUID.fromString(request.pathVariable("id"));

            Mono<Game> updated = Mono.zip(gameService.getGameById(id), request.bodyToMono(Game.class))
                    .flatMap(t -> gameService.updateGame(
                            t.getT2().withId(t.getT1().getId()))
                    );

            return ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON).body(updated, Game.class);
        }

    }


}
