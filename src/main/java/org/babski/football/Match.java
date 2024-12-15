package org.babski.football;

record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {

    Match {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("Home team cannot be null or blank");
        }
        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Away team cannot be null or blank");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams cannot be the same");
        }
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Home or away score cannot be negative");
        }
    }

    Match(String homeTeam, String awayTeam) {
        this(homeTeam, awayTeam, 0, 0);
    }

    @Override
    public String toString() {
        return String.format("%s %d - %s %d", homeTeam, homeScore, awayTeam, awayScore);
    }
}
