package com.laddco.branchdemo.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubUserResponse(
    String login,
    String name,
    @JsonProperty("avatar_url") String avatarUrl,
    String location,
    String email,
    String url,
    @JsonProperty("created_at") Instant createdAt
) {}
