package com.shin.leaderboard.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {
    @Id @With
    UUID id;
    String name;
    String platform;
    String creator;
}
