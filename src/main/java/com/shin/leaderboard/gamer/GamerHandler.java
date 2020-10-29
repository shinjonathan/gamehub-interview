package com.shin.leaderboard.gamer;

import com.shin.leaderboard.game.Game;
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
public class GamerHandler {
    @Bean
    RouterFunction<?> gamerRoutes(GamerRequestHandler requestHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/gamers"), requestHandler::getGamers)
                .andRoute(RequestPredicates.GET("/gamers/{id}"), requestHandler::getGamerById)
                .andRoute(RequestPredicates.POST("/gamers"), requestHandler::createGamer)
                .andRoute(RequestPredicates.POST("/gamers/{id}"), requestHandler::updateGamer);
    }

    @Component
    @RequiredArgsConstructor
    public static class GamerRequestHandler {

        private final GamerService gamerService;

        public Mono<ServerResponse> getGamers(ServerRequest request) {
            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(gamerService.getAll().collectList(), Gamer.class);
        }

        public Mono<ServerResponse> getGamerById(ServerRequest request) {
            UUID id = UUID.fromString(request.pathVariable("id"));

            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(gamerService.getGamerById(id), Game.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        }

        public Mono<ServerResponse> createGamer(ServerRequest request) {
            return request.bodyToMono(Gamer.class)
                    .flatMap(gamerService::createGamer)
                    .flatMap(r ->
                            ServerResponse
                                    .created(UriComponentsBuilder.fromPath("/gamers/{id}").build(r.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(Mono.just(r), Game.class));
        }


        public Mono<ServerResponse> updateGamer(ServerRequest request) {
            UUID id = UUID.fromString(request.pathVariable("id"));

            Mono<Gamer> updated = Mono.zip(gamerService.getGamerById(id), request.bodyToMono(Gamer.class))
                    .flatMap(t -> gamerService.updateGamer(t.getT2().withId(t.getT1().getId())));

            return ServerResponse
                    .accepted()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updated, Gamer.class);
        }

    }
}
