package entities;

import MatchMaking.Ranks;

public abstract class BaseUser {
    protected String discordID;
    protected String username;
    protected Ranks rank;
    protected int wins;
    protected int losses;
    protected boolean inQueue;

    public BaseUser(String discordID, String username) {
        this.discordID = discordID;
        this.username = username;
        this.rank = Ranks.UNRANKED;
        this.wins = 0;
        this.losses = 0;
        this.inQueue = false;
    }

    //Methods used by child classes
    public abstract boolean canExecuteCommand(String command);
    public abstract String getDisplayName();
    public abstract String getUserType();


    //Getters and Setters
    public String getDiscordID(){
        return discordID;
    }

    public void setDiscordID(String discordID){
        this.discordID = discordID;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }
}
