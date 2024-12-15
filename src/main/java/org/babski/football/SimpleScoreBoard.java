package org.babski.football;


import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SimpleScoreBoard implements ScoreBoard {

    private final TreeSet<Match> matches = new TreeSet<>(
            Comparator.<Match, Integer>comparing(match -> match.homeScore() + match.awayScore()).reversed()
                    .thenComparing(Match::startTime, Comparator.reverseOrder()));
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
        matches.add(new Match(homeTeam, awayTeam, clock.instant()));
    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        Match existingMatch = findMatchOrThrow(homeTeam, awayTeam);
        matches.remove(existingMatch);
        matches.add(new Match(homeTeam, awayTeam, homeScore, awayScore, existingMatch.startTime()));
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        matches.remove(findMatchOrThrow(homeTeam, awayTeam));
    }

    @Override
    public List<String> getSummary() {
        return matches.stream()
                .map(Match::toString)
                .toList();
    }


    private Match findMatchOrThrow(String homeTeam, String awayTeam) {
        return matches.stream()
                .filter(match -> homeTeam.equals(match.homeTeam()) && awayTeam.equals(match.awayTeam()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s - %s match not found in score board", homeTeam, awayTeam)));
    }

    private class ScoreBoardValidator {
        void validateTeamsAreNotInScoreboard(String homeTeam, String awayTeam) {
            String existingTeams = matches.stream()
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
