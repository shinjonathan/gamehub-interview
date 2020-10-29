package com.shin.leaderboard.gamer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GamerService {
    private final GamerRepository gamerRepository;

    public Flux<Gamer> getAll() {
        return gamerRepository.findAll();
    }

    public Mono<Gamer> getGamerById(UUID id) {
        return gamerRepository.findById(id);
    }

    public Mono<Gamer> createGamer(Gamer gamer) {
        return gamerRepository.save(gamer);
    }

    public Mono<Gamer> updateGamer(Gamer gamer) {
        return gamerRepository.save(gamer);
    }
}
