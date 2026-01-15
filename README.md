# Branch Demo

A simple spring boot app that fetches github user data and repos to combine the essential information into one response.

## Prerequisites
- Java 21 (I recommend using [sdkman](https://sdkman.io/) to install it)

## Running the Application
From repository root
```bash
./gradlew bootRun
```

The server starts at `http://localhost:8080`

## API Usage

### Get GitHub Data

Fetches user info and repositories from GitHub.

```bash
curl "http://localhost:8080/github-data?username=octocat"
```

### Example Response

```json
{
  "user_name": "octocat",
  "display_name": "The Octocat",
  "avatar": "https://avatars.githubusercontent.com/u/583231?v=4",
  "geo_location": "San Francisco",
  "email": null,
  "url": "https://api.github.com/users/octocat",
  "created_at": "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos": [
    {
      "name": "boysenberry-repo-1",
      "url": "https://api.github.com/repos/octocat/boysenberry-repo-1"
    }
  ]
}
```

## Running Tests

```bash
./gradlew test
```
