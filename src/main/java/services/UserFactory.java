package services;

import entities.Administrator;
import entities.BaseUser;
import entities.RegularUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import utils.ConfigManager;

public class UserFactory {
    public static BaseUser createUser(Member member) {
        String discordID = member.getId();
        String username = member.getEffectiveName();

        //Check if the user is an owner of the bot
        if(isBotOwner(discordID)){
            return new Administrator(discordID, username, true);
        }
        //Check if the user has a permission by administrator
        if(hasAdminPermissions(member)) {
            return new Administrator(discordID, username, false);
        }
        //Regular user
        return new RegularUser(discordID, username);
    }
    private static boolean isBotOwner(String discordID) {
        return discordID.equals(ConfigManager.getBotOwnerId());    // Check if the user is the bot owner
    }
    // Check if the user has admin permissions
    private static boolean hasAdminPermissions(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR) ||
                member.hasPermission(Permission.MANAGE_SERVER) ||
                member.getRoles().stream().anyMatch(role ->
                        role.getName().toLowerCase().contains("admin") ||
                                role.getName().toLowerCase().contains("mod")
                );
    }
}
