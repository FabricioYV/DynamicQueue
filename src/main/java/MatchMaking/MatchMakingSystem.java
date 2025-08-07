package MatchMaking;

import entities.BaseUser;
import net.dv8tion.jda.api.entities.Member;
import services.UserDAO;
import services.UserFactory;
import utils.ConfigManager;

import java.util.*;
import java.util.concurrent.*;

public class MatchMakingSystem {
    private final UserDAO userDAO;
    private final ScheduledExecutorService scheduler;
    private final Map<String, Match> activeMatches;
    private final Map<Integer,ScheduledFuture<?>> pendingCountdowns;
    private List<Member> currentQueueMembers;
    public MatchMakingSystem(UserDAO userDAO){
        this.userDAO = userDAO;
        this.scheduler = Executors.newScheduledThreadPool(5);  //Pool size can be adjusted based on expected load
        this.activeMatches = new ConcurrentHashMap<>();
        this.pendingCountdowns = new ConcurrentHashMap<>();
        this.currentQueueMembers = new ArrayList<>();
    }
    public void updateQueue(List<Member> members){
        this.currentQueueMembers = new ArrayList<>(members);
        int totalMembers = members.size();

        ConfigManager.logger("Updating queue with " + totalMembers + " members");

        //Check size of the game is possible
        List<Integer> possibleMatches = getPossibleMatchSizes(totalMembers);

        if(possibleMatches.isEmpty()){
            cancelAllPendingCountdowns();
            if (totalMembers < 6){
                ConfigManager.logger("Waiting for more members to start a match");
            }else{
                ConfigManager.logger("odd number waiting for one more ");
            }
            return;
        }

        //Update or create countdowns for possible matches
        for(Integer playerCount : possibleMatches){
            if(!pendingCountdowns.containsKey(playerCount)){
                startCountdownForSize(playerCount);
            }
        }

        //Cancel countdowns for sizes that are no longer possible
        Set<Integer> toRemove = new HashSet<>();
        for(Integer size : pendingCountdowns.keySet()){
            if(!possibleMatches.contains(size)){
                pendingCountdowns.get(size).cancel(false);
                toRemove.add(size);
                ConfigManager.logger("Cancelled countdown for match of size " + size);
            }
        }
        toRemove.forEach(pendingCountdowns::remove);


    }
    private List<Integer> getPossibleMatchSizes(int totalMembers){
        List<Integer> possible = new ArrayList<>();

        for(int i = 6; i<= totalMembers; i+=2){
          possible.add(i);
        }
        return possible;
    }

    private void startCountdownForSize(int playerCount){
        ConfigManager.logger("Starting countdown to 120 seconds for  " + playerCount);

        ScheduledFuture<?> countdown = scheduler.schedule(() -> {
            if(currentQueueMembers.size() >= playerCount){
                startMatch(playerCount);
            }
            pendingCountdowns.remove(playerCount);
        },120, TimeUnit.SECONDS);

        pendingCountdowns.put(playerCount,countdown);

    }
    private void startMatch(int playerCount){
        if(currentQueueMembers.size() < playerCount){
            ConfigManager.logger("Not enough players to start a match");
            return;
        }


        // Choose the first free players.
        List<Member> selectedMembers = currentQueueMembers.subList(0, playerCount);  //Choose from 0 to playerCount{
        List<BaseUser> players = convertMembersToUsers(selectedMembers);


        if(players.size() != playerCount){
            ConfigManager.logger("Not enough players to start a match");
            return;
        }
        //Create balanced teams
        List<List<BaseUser>> teams = createBalancedTeams(players);

        //Generate unique match ID
        String matchId = generateMatchId(playerCount);

        //Remove selected members from the queue
        currentQueueMembers = currentQueueMembers.subList(playerCount, currentQueueMembers.size());

        //Create match object
        Match match = new Match(matchId,teams.get(0),teams.get(1),playerCount,null);
        activeMatches.put(matchId,match);

        ConfigManager.logger("Starting match with id " + matchId);
        ConfigManager.logger("Team 1: " + teams.get(0).stream().map(BaseUser::getUsername).reduce("",(a,b)->a+b+","));
        ConfigManager.logger("Team 2: " + teams.get(1).stream().map(BaseUser::getUsername).reduce("",(a,b)->a+b+","));
        ConfigManager.logger("Players: " + players.stream().map(BaseUser::getUsername).reduce("",(a,b)->a+b+","));
        ConfigManager.logger("Match will start in 120 seconds");

        //Remove the completed countdown
        pendingCountdowns.remove(playerCount);

        //Update the queue after remove players.

        updateQueue(currentQueueMembers);
    }
    private List<BaseUser> convertMembersToUsers(List<Member> members){
        List<BaseUser> players = new ArrayList<>();

        for(Member member : members){
            BaseUser user = UserFactory.createUser(member);  //Create BaseUser from a discord member.
            if(userDAO.userExists(user.getDiscordID())){
                BaseUser dbUser = userDAO.getUser(user.getDiscordID());
                if(dbUser != null){
                    players.add(dbUser);
                }else{
                    ConfigManager.logger("User " + user.getUsername() + " not found in database");
                    return new ArrayList<>();  //return null array.
                }
            }else{
                ConfigManager.logger("User " + user.getUsername() + " is not registered");
                return new ArrayList<>();  //return null array.
            }

        }
        return players;
    }
    private List<List<BaseUser>> createBalancedTeams(List<BaseUser> players){
        //Sort Players to rank for best balanced
        players.sort((p1, p2) -> Integer.compare(p2.getRank().getRankId(), p1.getRank().getRankId()));

        List<BaseUser> team1 = new ArrayList<>();
        List<BaseUser> team2 = new ArrayList<>();

        //alternating distribution for basic equilibrium
        for(int i = 0; i<players.size(); i++){
            if(i % 2 == 0){
                team1.add(players.get(i));
            }else{
                team2.add(players.get(i));
            }
        }
        return Arrays.asList(team1,team2);
    }
    private String generateMatchId(int playerCount){
        return "Match " +  (playerCount/2) + "v" + (playerCount/2) + " - " + System.currentTimeMillis();
    }

    private void cancelAllPendingCountdowns(){
        for(ScheduledFuture<?> countdown : pendingCountdowns.values()){
            if(countdown != null && !countdown.isDone()){
                countdown.cancel(false);
            }
        }
        pendingCountdowns.clear();
        ConfigManager.logger("All pending countdowns cancelled");
    }
    public void endMatch(String matchId){
        Match match = activeMatches.remove(matchId);
        if(match != null){
            ConfigManager.logger("Match " + matchId + " ended");
        }else{
            ConfigManager.logger("Match " + matchId + " not found");
        }
    }
    public Map<String, Match> getActiveMatches() {
        return new HashMap<>(activeMatches);

    }
    public void shutdown() {
        cancelAllPendingCountdowns();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        ConfigManager.logger("MatchMakingSystem shutdown complete");
    }
}