# dbassist

A Spring Boot backend for MongoDB schema summarization and Gemini-based query generation.

## Overview

This project connects to a MongoDB database, inspects collection samples, generates a plain-English schema summary, and uses that summary to answer user questions via Google Gemini.

## Requirements

- Java 20
- Gradle
- MongoDB connection string and database name
- Optional Gemini API key (configured via `gemini.api.key` or environment variable if the service is updated)

## Build

```bash
./gradlew.bat build
```

## Run

```bash
./gradlew.bat bootRun
```

The application starts on `http://localhost:8080` by default.

## REST Endpoints

### Health check

`GET /api/health`

Returns a simple status string.

### Generate MongoDB schema summary

`POST /api/mongo/schema-summary`

Request body:

```json
{
  "connectionString": "mongodb+srv://...",
  "databaseName": "yourDatabase"
}
```

Response body contains:

- `success` - whether summary generation succeeded
- `message` - status message
- `databaseName` - the requested database
- `collections` - list of collections found
- `schemaSummary` - plain-English schema summary

### Ask a question using schema summary

`POST /api/query`

Request body:

```json
{
  "schemaSummary": "...optional summary...",
  "question": "Write a SQL query for ..."
}
```

If `schemaSummary` is omitted or empty, the service uses the last generated schema summary stored in memory.

Response returns a generated SQL query or answer.

## Notes

- Schema summaries are stored in memory and reused for later queries.
- The app currently uses Google Gemini via `WebClient` to generate responses.
- Update `application.properties` or the `GeminiService` implementation to configure the Gemini API key and model if needed.
