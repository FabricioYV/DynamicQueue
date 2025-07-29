package MatchMaking;

public enum Ranks {
    UNRANKED(0, "Unranked"),
    BRONZE(1, "Bronze"),
    SILVER(2, "Silver"),
    GOLD(3, "Gold"),
    PLATINUM(4, "Platinum"),
    DIAMOND(5, "Diamond"),
    MASTER(6, "Master"),
    GRANDMASTER(7, "Grandmaster");

    private final int rankId;
    private final String rankName;

    Ranks(int rankId, String rankName) {
        this.rankId = rankId;
        this.rankName = rankName;
    }

    public int getRankId() {
        return rankId;
    }

    public String getRankName() {
        return rankName;
    }
}
