package org.babski.football;


import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SimpleScoreBoard implements ScoreBoard {

    private final Map<Instant, Match> matches = new HashMap<>();
    private final Clock clock;

    public SimpleScoreBoard() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        matches.put(clock.instant(), new Match(homeTeam, awayTeam));
    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {

    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {

    }

    @Override
    public List<String> getSummary() {
        return matches.values().stream()
                .map(Match::toString)
                .toList();
    }
}
