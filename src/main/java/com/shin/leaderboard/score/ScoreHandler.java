package com.shin.leaderboard.score;

import com.shin.leaderboard.game.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Configuration
public class ScoreHandler {

    @Bean
    RouterFunction<?> scoreRoutes(ScoreRequestHandler requestHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/gamers/{gamerId}/games"), requestHandler::getGamesPlayed)
                .andRoute(RequestPredicates.GET("/gamers/{gamerId}/games/{gameId}"), requestHandler::getScores)
                .andRoute(RequestPredicates.POST("/gamers/{gamerId}/games/{gameId}"), requestHandler::createScore)
                .andRoute(RequestPredicates.GET("/games/{gameId}/gamers"), requestHandler::getGamersForGame)
                .andRoute(RequestPredicates.GET("/games/{gameId}/metrics"), requestHandler::getAverageScoreForGame)
                /* TODO: This router function should probably be combined with /games */
                .andRoute(RequestPredicates.GET("/popular"), requestHandler::getGamesByPopularity);
    }

    @Component
    @RequiredArgsConstructor
    public static class ScoreRequestHandler {

        private final ScoreService scoreService;

        public Mono<ServerResponse> getGamesPlayed(ServerRequest serverRequest) {
            UUID gamerId = UUID.fromString(serverRequest.pathVariable("gamerId"));

            return ServerResponse.ok().body(scoreService.getGamesPlayed(gamerId), Game.class);
        }

        public Mono<ServerResponse> getScores(ServerRequest serverRequest) {
            UUID gamerId = UUID.fromString(serverRequest.pathVariable("gamerId"));
            UUID gameId = UUID.fromString(serverRequest.pathVariable("gameId"));

            return ServerResponse.ok().body(scoreService.getScores(gamerId, gameId), Score.class);
        }

        public Mono<ServerResponse> createScore(ServerRequest serverRequest) {
            UUID gamerId = UUID.fromString(serverRequest.pathVariable("gamerId"));
            UUID gameId = UUID.fromString(serverRequest.pathVariable("gameId"));

            Mono<Score> score = serverRequest.bodyToMono(Score.class)
                    .map(r -> {
                        if (r.getScore() > 5 || r.getScore() < 0)
                            throw new ResponseStatusException(BAD_REQUEST, "Invalid request");
                        else
                            return new Score(null, gameId, gamerId, r.getScore());
                    })
                    .flatMap(scoreService::addScore);

            return ServerResponse.accepted().body(score, Score.class);
        }

        public Mono<ServerResponse> getGamersForGame(ServerRequest serverRequest) {
            UUID gameId = UUID.fromString(serverRequest.pathVariable("gameId"));

            return ServerResponse.ok().body(scoreService.getGamersForGame(gameId), Score.class);
        }

        public Mono<ServerResponse> getAverageScoreForGame(ServerRequest serverRequest) {
            UUID gameId = UUID.fromString(serverRequest.pathVariable("gameId"));

            return ServerResponse.ok().body(scoreService.getAverageScoreForGame(gameId), Score.class);
        }

        public Mono<ServerResponse> getGamesByPopularity(ServerRequest serverRequest) {
            return ServerResponse.ok().body(scoreService.getGamesByPopularity().take(1), Game.class);
        }

    }
}
