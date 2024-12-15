package org.babski.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}