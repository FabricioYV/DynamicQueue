package MatchMaking;

import entities.BaseUser;
import utils.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class Match {
    private final String matchId;
    private final List<BaseUser> team1;
    private final List<BaseUser> team2;
    private final int totalPlayers;
    private final ScheduledFuture<?> countdown;
    private final long startTime;
    private MatchResult result;
    private boolean isCompleted;

    public enum MatchResult {
        TEAM1_WIN, TEAM2_WIN, DRAW
    }
    public void setResult(MatchResult result) {
        this.result = result;
        this.isCompleted = true;
    }

    public MatchResult getResult() {
        return result;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    //End
    public Match(String matchId, List<BaseUser> team1, List<BaseUser> team2, int totalPlayers, ScheduledFuture<?> countdown) {
        this.matchId = matchId;
        this.team1 = new ArrayList<>(team1);
        this.team2 = new ArrayList<>(team2);
        this.totalPlayers = team1.size() + team2.size();
        this.countdown = countdown;
        this.startTime = System.currentTimeMillis();
    }

    //Getters for match details
    public String getMatchId() {
        return matchId;
    }
    public List<BaseUser> getTeam1() {
        return team1;
    }
    public List<BaseUser> getTeam2() {
        return team2;
    }
    public int getTotalPlayers() {
        return totalPlayers;
    }
    public boolean isActive(){
        return countdown != null && !countdown.isDone();
    }

    public void cancelCountdown() {
        if (countdown != null && !countdown.isDone()) {
            countdown.cancel(false);
            ConfigManager.logger("Countdown cancelled for match: " + matchId);
        }
    }

}
