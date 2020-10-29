package com.shin.leaderboard.gamer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gamer {
    @Id @With
    UUID id;
    String firstName;
    String lastName;
    String email;
}
