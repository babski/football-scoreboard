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
        Match updatedMatch = new Match(homeTeam, awayTeam, homeScore, awayScore);
        matches.put(findMatchInstantOrThrow(homeTeam, awayTeam), updatedMatch);
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

    private Instant findMatchInstantOrThrow(String homeTeam, String awayTeam) {
        return matches.entrySet().stream()
                .filter(matchEntry -> homeTeam.equals(matchEntry.getValue().homeTeam()) && awayTeam.equals(matchEntry.getValue().awayTeam()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s - %s match not found in score board", homeTeam, awayTeam)));
    }
}
