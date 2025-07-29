package entities;

public class Administrator extends BaseUser {

    private boolean isBotOwner;
    private String adminLevel;

    public Administrator(String discordID, String username,boolean isBotOwner) {
        // Call the constructor of the parent class BaseUser
        super(discordID, username);
        this.isBotOwner = isBotOwner;
        this.adminLevel = isBotOwner ? "OWNER" : "ADMIN";

    }

    @Override
    public boolean canExecuteCommand(String command) {
        //Administrators can execute all commands
        return true;

    }
    @Override
    public String getUserType() {
        return adminLevel;
    }
    @Override
    public String getDisplayName() {
        String emoji = isBotOwner ? "👑" : "🛡️";
        return emoji + " " + username + " (" + adminLevel + ")";
    }
    //Specific methods for administrators
    public boolean canBanUsers(){
        return true;
    }
    public boolean canManageQueue(){
        return true;
    }
    public boolean canViewAllStats(){
        return true;
    }
    public boolean canResetUserData(){
        return isBotOwner;

    }

    public boolean isBotOwner() {
        return isBotOwner;
    }

    public void setBotOwner(boolean botOwner) {
        isBotOwner = botOwner;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
}
