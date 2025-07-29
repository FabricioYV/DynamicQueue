import manager.DiscordBotManager;

public class DynamicQueue{
    //We are using Java's main method but we could also use a different entry point
    public static void main(String[] args) {
        //Install JDA whit maven for use.
        //Start the Discord bot
        DiscordBotManager dc = new DiscordBotManager();
        dc.StartBot();

    }

}