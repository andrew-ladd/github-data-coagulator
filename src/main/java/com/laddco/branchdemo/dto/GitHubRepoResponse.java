package com.laddco.branchdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoResponse(
    String name,
    String url
) {}
