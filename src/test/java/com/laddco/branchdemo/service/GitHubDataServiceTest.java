package com.laddco.branchdemo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.laddco.branchdemo.dto.GitHubDataResponse;
import com.laddco.branchdemo.exception.GitHubDataException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GitHubDataServiceTest {

    private GitHubDataService gitHubDataService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        gitHubDataService = new GitHubDataService(restClientBuilder);
    }

    @Test
    void getGitHubData_returnsUserAndRepos() throws IOException {
        String userResponse = loadResource("github-user-response.json");
        String reposResponse = loadResource("github-repos-response.json");

        mockServer.expect(requestTo("https://api.github.com/users/octocat"))
                .andRespond(withSuccess(userResponse, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("https://api.github.com/users/octocat/repos"))
                .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

        GitHubDataResponse response = gitHubDataService.getGitHubData("octocat");

        assertEquals("octocat", response.userName());
        assertEquals("The Octocat", response.displayName());
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", response.avatar());
        assertEquals("San Francisco", response.geoLocation());
        assertNull(response.email());
        assertEquals("https://api.github.com/users/octocat", response.url());
        assertEquals(Instant.parse("2011-01-25T18:44:36Z"), response.createdAt());
        assertEquals(2, response.repos().size());
        assertEquals("boysenberry-repo-1", response.repos().get(0).name());
        assertEquals("https://api.github.com/repos/octocat/boysenberry-repo-1", response.repos().get(0).url());
        assertEquals("git-consortium", response.repos().get(1).name());

        mockServer.verify();
    }

    @Test
    void getGitHubData_userNotFound_throwsException() throws IOException {
        String notFoundResponse = loadResource("github-not-found-response.json");

        mockServer.expect(requestTo("https://api.github.com/users/nonexistent"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(notFoundResponse));

        GitHubDataException exception = assertThrows(GitHubDataException.class, () ->
                gitHubDataService.getGitHubData("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Not Found"));

        mockServer.verify();
    }

    @Test
    void getGitHubData_reposEndpointFails_throwsException() throws IOException {
        String userResponse = loadResource("github-user-response.json");

        mockServer.expect(requestTo("https://api.github.com/users/octocat"))
                .andRespond(withSuccess(userResponse, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("https://api.github.com/users/octocat/repos"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\": \"Server Error\"}"));

        GitHubDataException exception = assertThrows(GitHubDataException.class, () ->
                gitHubDataService.getGitHubData("octocat"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());

        mockServer.verify();
    }

    @Test
    void getGitHubData_rateLimited_throwsException() throws IOException {
        String rateLimitResponse = loadResource("github-rate-limit-response.json");

        mockServer.expect(requestTo("https://api.github.com/users/octocat"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(rateLimitResponse));

        GitHubDataException exception = assertThrows(GitHubDataException.class, () ->
                gitHubDataService.getGitHubData("octocat"));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("rate limit"));

        mockServer.verify();
    }

    private String loadResource(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
