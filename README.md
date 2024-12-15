# Live Football World Cup Scoreboard Library

This is a simple library for managing and displaying live football match scores, following the requirements outlined in the coding exercise.

## Functionality

The library supports the following operations:

1.  **Start a New Match:**
    *   Initializes a new match with a score of 0-0.
    *   Requires the names of the home and away teams.
    *   Records the start time of the match.
2.  **Update Score:**
    *   Updates the score of an existing match.
    *   Requires the names of the home and away teams, and their new scores.
    *   Maintains the original start time of the match.
3.  **Finish Match:**
    *   Removes a match from the scoreboard.
    *   Requires the names of the home and away teams.
4.  **Get Summary:**
    *   Retrieves a list of matches in progress, ordered by total score (highest score first).
    *   Matches with the same total score are ordered by the most recently started.

## Implementation Details

*   **In-Memory Storage:** The library uses a `TreeSet` to store match data in memory.
*   **Match Representation:** The `Match` record holds information about a single match, including the start time.
*   **Scoreboard Interface:** The `ScoreBoard` interface defines the operations.
*   **SimpleScoreBoard Implementation:** The `SimpleScoreBoard` class implements the `ScoreBoard` interface.
*   **Test-Driven Development:** The code was developed using Test-Driven Development principles, with JUnit tests included.
*   **Validation:** There is a simple validation mechanism in place to ensure that teams are not in multiple matches at the same time.

## How to Use

1.  Create an instance of the `ScoreBoard` using the factory method:
    ```java
    ScoreBoard scoreBoard = ScoreBoard.getSimpleScoreBoard();
    ```

2.  Use the methods `startMatch`, `updateScore`, `finishMatch`, and `getSummary` to interact with the scoreboard.

    ```java
    scoreBoard.startMatch("Mexico", "Canada");
    scoreBoard.updateScore("Mexico", "Canada", 1, 2);
    scoreBoard.finishMatch("Mexico", "Canada");
    List<String> summary = scoreBoard.getSummary();
    ```

## Assumptions

*   Team names cannot be null or blank.
*   Home and away teams cannot be the same.
*   Scores cannot be negative.
*   It is assumed that the team names are unique (no two teams with the same name).

## Noteworthy points

*   The implementation focuses on the core requirements and uses simple in-memory data storage.
*   The solution does not include error handling that goes beyond throwing IllegalArgumentException.

## Thread Safety

**Important:** This implementation is **NOT THREAD-SAFE**. Concurrent modifications to the scoreboard from multiple threads will likely result in unpredictable behavior and data corruption. If thread safety is a requirement, appropriate synchronization mechanisms (e.g., using `ConcurrentHashMap` or other thread-safe collections, and `synchronized` blocks or other synchronisation) will be needed.

## Notes

*   The code is designed to be simple and focused on the given requirements and does not provide an extensive feature set.
*   The `TreeSet` is used to store matches in sorted order, which makes `getSummary` more efficient as it doesn't need to sort the data each time.
*   The `Match` record includes the start time (`Instant`) of the match, which is used for ordering matches that have the same total score.
*   The tests demonstrate the expected behaviour of the application and include edge cases.
*   A `Clock` is used to determine the time when a match is started. It is possible to provide a custom clock for testing purposes.