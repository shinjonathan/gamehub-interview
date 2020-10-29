package com.shin.leaderboard.gamer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GamerRepository extends ReactiveCrudRepository<Gamer, UUID> {
}
