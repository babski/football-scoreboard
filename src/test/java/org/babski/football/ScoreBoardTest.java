package org.babski.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void matchCreationWithNullOrBlankHomeTeamThrowsAnException(String homeTeam) {
        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(homeTeam, ANY_AWAY_TEAM));
        assertEquals("Home team cannot be null or blank", exception.getMessage());
        assertEquals(0, scoreBoard.getSummary().size());
    }

    static Stream<String> blankAndNullStringProvider() {
        return Stream.of("", " ", null);
    }

}