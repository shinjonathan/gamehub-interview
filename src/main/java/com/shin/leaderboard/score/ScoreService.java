package com.shin.leaderboard.score;


import com.shin.leaderboard.game.Game;
import com.shin.leaderboard.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GameService gameService;

    private final ScoreRepository scoreRepository;

    public Flux<Game> getGamesPlayed(UUID gamerId) {
        return scoreRepository.getByGamerId(gamerId)
                .distinct(Score::getGameId)
                .flatMap(g -> gameService.getGameById(g.getGameId()));
    }

    public Flux<Score> getScores(UUID gamerId, UUID gameId) {
        return scoreRepository.getAllByGamerIdAndGameId(gamerId, gameId);
    }

    public Flux<Game> getGamesByPopularity() {
        return scoreRepository.findAll()
                .collect(Collectors.groupingBy(Score::getGameId, Collectors.counting()))
                .map(t -> t.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable)
                .flatMap(t -> gameService.getGameById(t.getKey()));
    }

    public Mono<Score> addScore(Score score) {
        return scoreRepository.save(score);
    }

    public Flux<Score> getGamersForGame(UUID gameId) {
        return scoreRepository.getByGameId(gameId)
                .sort(Comparator.comparing(Score::getScore).reversed()) /* Then compare by time */
                .distinct(Score::getGamerId);
    }

    public Mono<Score> getAverageScoreForGame(UUID gameId) {
        return scoreRepository.getByGameId(gameId)
                .collect(Collectors.averagingInt(Score::getScore))
                .map(r -> new Score(null, gameId, null, r.intValue()));
    }

}
