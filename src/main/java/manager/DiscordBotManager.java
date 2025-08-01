package manager;

import Listeners.QueueVoiceChannelListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import services.DatabaseManager;
import utils.ConfigManager;

public class DiscordBotManager {
    private DatabaseManager dbManager;
    private JDA jda;

    public void StartBot()  {
        // Code to initialize and start the Discord bot
        try{
            //Initialize database connection
            dbManager = new DatabaseManager();
            if(!dbManager.initialize()){
                ConfigManager.logger("Error connecting to the database");
                return;
            }

            jda = JDABuilder.createDefault(ConfigManager.getToken())
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS
                    )
                    .setActivity(Activity.playing(ConfigManager.getMessageActivity()))
                    .addEventListeners(new QueueVoiceChannelListener(jda))
                    .build();

            //Wait for the bot to be ready
            jda.awaitReady();
        }catch (Exception e){
            ConfigManager.logger("Error starting Discord bot: " + e.getMessage());
            if(e.getMessage().contains("401")){
                ConfigManager.logger("Check your token");
            }else if(e.getMessage().contains("50001")) {
                ConfigManager.logger("Check your intents or permissions");
            }
        }




    }

}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
