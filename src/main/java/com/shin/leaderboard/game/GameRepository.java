package com.shin.leaderboard.game;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends ReactiveCrudRepository<Game, UUID> {
}
