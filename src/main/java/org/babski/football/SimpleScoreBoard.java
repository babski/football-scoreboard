package org.babski.football;


import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SimpleScoreBoard implements ScoreBoard {

    private final Map<Instant, Match> matches = new HashMap<>();
    private final ScoreBoardValidator scoreBoardValidator = new ScoreBoardValidator();
    private Clock clock;

    SimpleScoreBoard() {
        this.clock = Clock.systemUTC();
    }

    void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        scoreBoardValidator.validateTeamsAreNotInScoreboard(homeTeam, awayTeam);
        matches.put(clock.instant(), new Match(homeTeam, awayTeam));
    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        Match updatedMatch = new Match(homeTeam, awayTeam, homeScore, awayScore);
        matches.put(findMatchInstantOrThrow(homeTeam, awayTeam), updatedMatch);
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        matches.remove(findMatchInstantOrThrow(homeTeam, awayTeam));
    }

    @Override
    public List<String> getSummary() {
        return matches.entrySet().stream()
                .sorted(Comparator.comparing(this::getMatchTotalScore).reversed()
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(entry -> entry.getValue().toString())
                .toList();
    }

    private int getMatchTotalScore(Map.Entry<Instant, Match> entry) {
        return entry.getValue().homeScore() + entry.getValue().awayScore();
    }

    private Instant findMatchInstantOrThrow(String homeTeam, String awayTeam) {
        return matches.entrySet().stream()
                .filter(matchEntry -> homeTeam.equals(matchEntry.getValue().homeTeam()) && awayTeam.equals(matchEntry.getValue().awayTeam()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s - %s match not found in score board", homeTeam, awayTeam)));
    }

    private class ScoreBoardValidator {
        void validateTeamsAreNotInScoreboard(String homeTeam, String awayTeam) {
            String existingTeams = matches.values().stream()
                    .filter(isAnyTeamInScoreBoard(homeTeam, awayTeam))
                    .flatMap(match -> Stream.of(match.homeTeam(), match.awayTeam()))
                    .filter(team -> team.equals(homeTeam) || team.equals(awayTeam))
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(" and "));

            if (!existingTeams.isEmpty()) {
                throw new IllegalArgumentException(String.format("%s team(s) already included in scoreboard", existingTeams));
            }
        }

        private Predicate<Match> isAnyTeamInScoreBoard(String homeTeam, String awayTeam) {
            return match -> match.homeTeam().equals(homeTeam) || match.awayTeam().equals(homeTeam)
                    || match.homeTeam().equals(awayTeam) || match.awayTeam().equals(awayTeam);
        }
    }
}
