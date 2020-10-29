package com.shin.leaderboard.score;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Score {
    @Id @JsonIgnore
    Long id;
    UUID gameId;
    UUID gamerId;
    int score;
}
