package com.laddco.branchdemo.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubDataResponse(
    @JsonProperty("user_name") String userName,
    @JsonProperty("display_name") String displayName,
    String avatar,
    @JsonProperty("geo_location") String geoLocation,
    String email,
    String url,
    @JsonProperty("created_at")
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss z", timezone = "GMT")
    Instant createdAt,
    List<Repo> repos
) {
    public record Repo(
        String name,
        String url
    ) {}
}
