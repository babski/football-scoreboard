package org.babski.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ScoreBoardTest {

    ScoreBoard scoreBoard;
    static final String ANY_HOME_TEAM = "Mexico";
    static final String ANY_AWAY_TEAM = "Canada";

    @BeforeEach
    void setUp() {
        scoreBoard = ScoreBoard.getSimpleScoreBoard();
    }

    @Test
    void matchStartsWithGoallessScore() {
        // When
        scoreBoard.startMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM);

        // Then
        assertEquals(1, scoreBoard.getSummary().size());
        assertEquals("Mexico 0 - Canada 0", scoreBoard.getSummary().getFirst());
    }

    @ParameterizedTest
    @MethodSource("blankAndNullStringProvider")
    void startingMatchForNullOrBlankHomeTeamThrowsAnException(String homeTeam) {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(homeTeam, ANY_AWAY_TEAM));
        assertEquals("Home team cannot be null or blank", exception.getMessage());
        assertEquals(0, scoreBoard.getSummary().size());
    }

    @ParameterizedTest
    @MethodSource("blankAndNullStringProvider")
    void startingMatchForNullOrBlankAwayTeamThrowsAnException(String awayTeam) {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(ANY_HOME_TEAM, awayTeam));
        assertEquals("Away team cannot be null or blank", exception.getMessage());
        assertEquals(0, scoreBoard.getSummary().size());
    }

    @Test
    void startingMatchForTheSameTeamsThrowsAnException() {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(ANY_HOME_TEAM, ANY_HOME_TEAM));
        assertEquals("Home and away teams cannot be the same", exception.getMessage());
        assertEquals(0, scoreBoard.getSummary().size());
    }

    @ParameterizedTest
    @CsvSource({"Mexico,Brazil,Mexico team(s) already included in scoreboard",
            "Brazil,Mexico,Mexico team(s) already included in scoreboard",
            "Canada,Brazil,Canada team(s) already included in scoreboard",
            "Brazil,Canada,Canada team(s) already included in scoreboard",
            "Canada,Mexico,Canada and Mexico team(s) already included in scoreboard"
    })
    void startingMatchForTeamsAlreadyInScoreBoardThrowsAnException(String homeTeam, String awayTeam, String message) {
        // Given
        scoreBoard.startMatch("Mexico", "Canada");

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(homeTeam, awayTeam));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void nonNegativeTeamsScoresAreUpdateOnScoreBoard() {
        // Given
        scoreBoard.startMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM);

        // When
        scoreBoard.updateScore(ANY_HOME_TEAM, ANY_AWAY_TEAM, 1, 2);

        // Then
        assertEquals(1, scoreBoard.getSummary().size());
        assertEquals("Mexico 1 - Canada 2", scoreBoard.getSummary().getFirst());
    }

    @ParameterizedTest
    @CsvSource({"-1, -1", "-1, 0", "0, -1"})
    void attemptToUpdateMatchWithNegativeScoresThrowsAnException(int homeScore, int awayScore) {
        // Given
        scoreBoard.startMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM);

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(ANY_HOME_TEAM, ANY_AWAY_TEAM, homeScore, awayScore));
        assertEquals("Home or away score cannot be negative", exception.getMessage());
    }

    @Test
    void attemptToUpdateMatchNotFoundInScoreBoardThrowsAnException() {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(ANY_HOME_TEAM, ANY_AWAY_TEAM, 2, 2));
        assertEquals("Mexico - Canada match not found in score board", exception.getMessage());
    }

    @Test
    void attemptToFinishMatchThatIsInScoreBoardEndsUpWithSuccess() {
        // Given
        scoreBoard.startMatch("Brazil", "Argentina");
        scoreBoard.startMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM);
        scoreBoard.startMatch("England", "Spain");

        // When
        scoreBoard.finishMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM);

        // Then
        assertEquals(2, scoreBoard.getSummary().size());
        assertTrue(scoreBoard.getSummary().contains("Brazil 0 - Argentina 0"));
        assertTrue(scoreBoard.getSummary().contains("England 0 - Spain 0"));
    }

    @Test
    void attemptToFinishMatchThatIsNotInOnScoreBoardThrowsAnException() {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.finishMatch(ANY_HOME_TEAM, ANY_AWAY_TEAM));
        assertEquals("Mexico - Canada match not found in score board", exception.getMessage());
    }

    @Test
    void gettingSummaryOfMatchesInProgressIsOrderedByTotalAndReturnedByMostRecentlyStarted() {
        // Given
        Clock mockClock = Mockito.mock(Clock.class);
        Instant now = Instant.now();
        AtomicInteger counter = new AtomicInteger(0);
        when(mockClock.instant()).thenAnswer(invocation -> now.plusSeconds(counter.getAndIncrement()));
        ((SimpleScoreBoard) scoreBoard).setClock(mockClock);

        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.startMatch("Germany", "France");
        scoreBoard.startMatch("Uruguay", "Italy");
        scoreBoard.startMatch("Argentina", "Australia");
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);
        scoreBoard.updateScore("Germany", "France", 2, 2);
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);
        scoreBoard.updateScore("Mexico", "Canada", 0, 5);

        // When
        List<String> summary = scoreBoard.getSummary();

        // Then
        assertEquals(5, summary.size());
        assertEquals("Uruguay 6 - Italy 6", summary.get(0));
        assertEquals("Spain 10 - Brazil 2", summary.get(1));
        assertEquals("Mexico 0 - Canada 5", summary.get(2));
        assertEquals("Argentina 3 - Australia 1", summary.get(3));
        assertEquals("Germany 2 - France 2", summary.get(4));
    }

    static Stream<String> blankAndNullStringProvider() {
        return Stream.of("", " ", null);
    }

}