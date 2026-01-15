package com.laddco.branchdemo.exception;

import org.springframework.http.HttpStatusCode;

public class GitHubDataException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String responseBody;

    public GitHubDataException(HttpStatusCode statusCode, String responseBody) {
        super(responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
