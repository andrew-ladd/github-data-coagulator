package com.laddco.branchdemo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.laddco.branchdemo.dto.GitHubDataResponse;
import com.laddco.branchdemo.dto.GitHubRepoResponse;
import com.laddco.branchdemo.dto.GitHubUserResponse;
import com.laddco.branchdemo.exception.GitHubDataException;

@Service
public class GitHubDataService {

    private static final Logger log = LoggerFactory.getLogger(GitHubDataService.class);
    private static final String GITHUB_API_BASE = "https://api.github.com";

    private final RestClient restClient;

    public GitHubDataService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .baseUrl(GITHUB_API_BASE)
            .build();
    }

    @Cacheable("gitHubData")
    public GitHubDataResponse getGitHubData(String username) {
        log.trace("Fetching GitHub data for user: {}", username);

        GitHubUserResponse user;
        try {
            user = restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubUserResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to fetch user data for {}: {} {}", username, e.getStatusCode(), e.getResponseBodyAsString());
            throw new GitHubDataException(e.getStatusCode(), e.getResponseBodyAsString());
        }

        List<GitHubRepoResponse> repos;
        try {
            repos = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubRepoResponse>>() {});
        } catch (RestClientResponseException e) {
            log.error("Failed to fetch repos for {}: {} {}", username, e.getStatusCode(), e.getResponseBodyAsString());
            throw new GitHubDataException(e.getStatusCode(), e.getResponseBodyAsString());
        }

        log.trace("Successfully fetched data for user: {}", username);
        return new GitHubDataResponse(
            user.login(),
            user.name(),
            user.avatarUrl(),
            user.location(),
            user.email(),
            user.url(),
            user.createdAt(),
            repos.stream()
                .map(repo -> new GitHubDataResponse.Repo(repo.name(), repo.url()))
                .toList()
        );
    }
}
