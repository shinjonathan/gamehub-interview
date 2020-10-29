package com.shin.leaderboard.score;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ScoreRepository extends ReactiveCrudRepository<Score, Long> {

    Flux<Score> getByGamerId(UUID gamerId);
    Flux<Score> getByGameId(UUID gameId);
    Flux<Score> getAllByGamerIdAndGameId(UUID gamerId, UUID gameId);
}
