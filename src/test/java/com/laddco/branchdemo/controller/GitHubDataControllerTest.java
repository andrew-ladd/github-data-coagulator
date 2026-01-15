package com.laddco.branchdemo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.laddco.branchdemo.dto.GitHubDataResponse;
import com.laddco.branchdemo.exception.GitHubDataException;
import com.laddco.branchdemo.service.GitHubDataService;

@WebMvcTest(GitHubDataController.class)
class GitHubDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubDataService gitHubDataService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void getGitHubData_returnsSuccessResponse() throws Exception {
        GitHubDataResponse response = new GitHubDataResponse(
            "octocat",
            "The Octocat",
            "https://avatars.githubusercontent.com/u/583231?v=4",
            "San Francisco",
            null,
            "https://api.github.com/users/octocat",
            Instant.parse("2011-01-25T18:44:36Z"),
            List.of(new GitHubDataResponse.Repo("repo1", "https://api.github.com/repos/octocat/repo1"))
        );

        when(gitHubDataService.getGitHubData("octocat")).thenReturn(response);

        mockMvc.perform(get("/github-data").param("username", "octocat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("octocat"))
                .andExpect(jsonPath("$.display_name").value("The Octocat"))
                .andExpect(jsonPath("$.repos[0].name").value("repo1"));
    }

    @Test
    void getGitHubData_notFound_passesThrough404() throws Exception {
        String errorBody = "{\"message\":\"Not Found\",\"documentation_url\":\"https://docs.github.com/rest\"}";
        when(gitHubDataService.getGitHubData("nonexistent"))
                .thenThrow(new GitHubDataException(HttpStatus.NOT_FOUND, errorBody));

        mockMvc.perform(get("/github-data").param("username", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorBody));
    }

    @Test
    void getGitHubData_rateLimited_passesThrough429() throws Exception {
        String errorBody = "{\"message\":\"API rate limit exceeded\"}";
        when(gitHubDataService.getGitHubData("octocat"))
                .thenThrow(new GitHubDataException(HttpStatus.TOO_MANY_REQUESTS, errorBody));

        mockMvc.perform(get("/github-data").param("username", "octocat"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string(errorBody));
    }

    @Test
    void getGitHubData_serverError_passesThrough500() throws Exception {
        String errorBody = "{\"message\":\"Internal Server Error\"}";
        when(gitHubDataService.getGitHubData("octocat"))
                .thenThrow(new GitHubDataException(HttpStatus.INTERNAL_SERVER_ERROR, errorBody));

        mockMvc.perform(get("/github-data").param("username", "octocat"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(errorBody));
    }

    @Test
    void getGitHubData_blankUsername_returns400() throws Exception {
        mockMvc.perform(get("/github-data").param("username", "   "))
                .andExpect(status().isBadRequest());
    }
}
