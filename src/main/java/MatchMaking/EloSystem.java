package MatchMaking;

public class EloSystem {
    private static final int K_FACTOR = 32;   //then you can edit it.
    private static final double BASE_RATING = 1000.0;

    public static EloResult calculateEloChange(double winnerElo,double losserElo){
        double expectedWinnerElo = (1.0 / (1.0 + Math.pow(10.0, (losserElo - winnerElo) / 400.0)));
        double expectedLosserElo = (1.0 / (1.0 + Math.pow(10.0, (winnerElo - losserElo) / 400.0)));

        int winnerChange = (int)Math.round(K_FACTOR * (1 - expectedWinnerElo));
        int losserChange = (int)Math.round(K_FACTOR * (0 - expectedLosserElo));

        return new EloResult(winnerChange,losserChange);
    }
    public static double getBaseRating(){
        return BASE_RATING;
    }
    public static class EloResult{
        private final int winnerChange;
        private final int losserChange;

        public EloResult(int winnerChange, int losserChange) {
            this.winnerChange = winnerChange;
            this.losserChange = losserChange;

        }
        public int getWinnerChange() {
            return winnerChange;
        }
        public int getLosserChange() {
            return losserChange;
        }
    }
}
