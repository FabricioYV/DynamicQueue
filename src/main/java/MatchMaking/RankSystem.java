package MatchMaking;

public class RankSystem {
    public static Ranks calculateRankFromElo(int elo){
        if (elo < 800) return Ranks.UNRANKED;
        else if (elo < 1000) return Ranks.BRONZE;
        else if (elo < 1200) return Ranks.SILVER;
        else if (elo < 1400) return Ranks.GOLD;
        else if (elo < 1600) return Ranks.PLATINUM;
        else if (elo < 1800) return Ranks.DIAMOND;
        else if (elo < 2000) return Ranks.MASTER;
        else return Ranks.GRANDMASTER;
    }

    public static boolean shouldPromote(Ranks currentRank,int newElo){
        Ranks calculatedRank = calculateRankFromElo(newElo);
        return calculatedRank.getRankId() > currentRank.getRankId();

    }
    public static boolean shouldDemote(Ranks currentRank,int newElo){
        Ranks calculatedRank = calculateRankFromElo(newElo);
        return calculatedRank.getRankId() < currentRank.getRankId();
    }
}
