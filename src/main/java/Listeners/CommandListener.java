package Listeners;

import MatchMaking.Match;
import MatchMaking.MatchMakingSystem;
import entities.Administrator;
import entities.BaseUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.DatabaseManager;
import services.UserDAO;
import services.UserFactory;
import utils.ConfigManager;

import java.awt.*;
import java.util.Map;

public class CommandListener extends ListenerAdapter {

    private UserDAO userDAO;
    private MatchMakingSystem matchMakingSystem;


    public CommandListener(DatabaseManager dbManager, MatchMakingSystem matchmakingsystem) {
        this.userDAO = new UserDAO(dbManager);
        this.matchMakingSystem = matchmakingsystem;

    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if(!message.startsWith(ConfigManager.getPrefix())) return;

        String [] args = message.substring(ConfigManager.getPrefix().length()).split(" ");
        String command = args[0].toLowerCase();

        BaseUser user = UserFactory.createUser(event.getMember());

        if (!user.canExecuteCommand(command)) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⛔ Acceso denegado")
                    .setDescription("No tienes permisos para ejecutar este comando.")
                    .setColor(Color.RED)
                    .setFooter("DynamicQueue", null);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        switch (command) {
            case "register", "registro" -> handleRegisterCommand(event, user);
            case "profile", "perfil" -> handleProfileCommand(event, user);
            case "ban" -> {

                if (user instanceof Administrator ) {
                    Administrator admin = (Administrator) user;
                    handleBanCommand(event, args, admin);
                }
            }
            case "reset" -> {
                if (user instanceof Administrator ) {
                    Administrator admin = (Administrator) user;
                    if(admin.canResetUserData()){
                        handleResetCommand(event, args, admin);
                    }
                }
            }
            case "matches","partidas" -> handleActiveMatchesCommand(event,user);
        }



    }
    private void handleRegisterCommand(MessageReceivedEvent event, BaseUser user) {
        if(userDAO.userExists(user.getDiscordID())){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ Ya estas registrado")
                    .setDescription("Ya estas registrado en DynamicQueue.")
                    .setColor(Color.ORANGE)
                    .setFooter("DynamicQueue", null);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        boolean success = userDAO.registerUser(user);
        if(success){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ Register Successful")
                    .setDescription("You have been successfully registered in DynamicQueue.")
                    .addField("User", user.getDisplayName(), true)
                    .addField("Type", user.getUserType(), true)
                    .addField("Initial Rank", user.getRank().getRankName(), true)
                    .setColor(Color.GREEN)
                    .setFooter("DynamicQueue", null);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
    private void handleProfileCommand(MessageReceivedEvent event, BaseUser user) {
        BaseUser dbUser = userDAO.getUser(user.getDiscordID());
        if(dbUser == null){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ The user does not exist")
                    .setDescription("You need to register first using the `!register` command.")
                    .setColor(Color.RED)
                    .setFooter("DynamicQueue", null);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Profile of " + dbUser.getDisplayName())
                .addField("Username", dbUser.getUsername(), true)
                .addField("Rank", dbUser.getRank().getRankName(), true)
                .addField("Wins", String.valueOf(dbUser.getWins()), true)
                .addField("Losses", String.valueOf(dbUser.getLosses()), true)
                .addField("In Queue", String.valueOf(dbUser.isInQueue()), true)
                .setColor(Color.BLUE)
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setFooter("DynamicQueue", null);

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
    private void handleBanCommand(MessageReceivedEvent event,String[] args,Administrator admin) {
        //Command exclusive for administrators
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Commando Ban")
                .setDescription("Ban Functionality executed by " + admin.getDisplayName())
                .setColor(Color.RED)
                .setFooter("DynamicQueue", null);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();


    }
    private void handleActiveMatchesCommand(MessageReceivedEvent event,BaseUser user) {
        //Command exclusive for administrators
        Map<String, Match> activeMatches = matchMakingSystem.getActiveMatches();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Active Matches")
                .setColor(Color.GREEN)
                .setFooter("DynamicQueue", null);
        if (activeMatches.isEmpty()) {
            embed.setDescription("No active matches");
        } else {
            StringBuilder description = new StringBuilder();
            description.append("**Active matches:** ").append(activeMatches.size()).append("\n\n");
            int matchCount = 1;
            for (Match match : activeMatches.values()) {
                description.append("**").append(matchCount).append(". ").append(match.getMatchId()).append("**\n");
                description.append("**Players:**").append(match.getTotalPlayers()).append(" (").append(match.getTeam1().size()).append(" vs ").append(match.getTeam2().size()).append(")\n");

                description.append("**Team 1:** ");
                description.append(match.getTeam1().stream()
                        .map(BaseUser::getUsername)
                        .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b));
                description.append("\n");

                description.append("**Team 2:** ");
                description.append(match.getTeam2().stream()
                        .map(BaseUser::getUsername)
                        .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b));
                description.append("\n");

                description.append("**Status:** ").append(match.isActive() ? "In countdown" : "Started").append("\n\n");
                matchCount++;
            }
            embed.setDescription(description.toString());
        }
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
    private void handleResetCommand(MessageReceivedEvent event, String[] args, Administrator admin) {
        //Command exclusive for owner bot
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Commando Reset")
                .setDescription("Reset Functionality executed by " + admin.getDisplayName())
                .setColor(Color.RED)
                .setFooter("DynamicQueue", null);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
