package com.shin.leaderboard.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Flux<Game> getAll() {
        return gameRepository.findAll();
    }

    public Mono<Game> getGameById(UUID id) {
        return gameRepository.findById(id);
    }

    public Mono<Game> createGame(Game game) {
        return gameRepository.save(game);
    }

    public Mono<Game> updateGame(Game game) {
        return gameRepository.save(game);
    }
}
