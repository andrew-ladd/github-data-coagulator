package com.laddco.branchdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laddco.branchdemo.dto.GitHubDataResponse;
import com.laddco.branchdemo.exception.GitHubDataException;
import com.laddco.branchdemo.service.GitHubDataService;

@RestController
public class GitHubDataController {

    private final GitHubDataService gitHubDataService;

    public GitHubDataController(GitHubDataService gitHubDataService) {
        this.gitHubDataService = gitHubDataService;
    }

    @GetMapping("/github-data")
    public GitHubDataResponse getGitHubData(@RequestParam String username) {
        return gitHubDataService.getGitHubData(username);
    }

    @ExceptionHandler(GitHubDataException.class)
    public ResponseEntity<String> handleGitHubDataException(GitHubDataException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBody());
    }
}
