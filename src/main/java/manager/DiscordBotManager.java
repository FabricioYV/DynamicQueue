package manager;

import Listeners.CommandListener;
import Listeners.QueueVoiceChannelListener;
import MatchMaking.Match;
import MatchMaking.MatchMakingSystem;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import services.DatabaseManager;
import services.UserDAO;
import utils.ConfigManager;

public class DiscordBotManager {
    private DatabaseManager dbManager;
    private JDA jda;
    private QueueVoiceChannelListener queuelistener;
    private MatchMakingSystem matchMakingSystem;
    public void StartBot()  {
        // Code to initialize and start the Discord bot
        try{
            //Initialize database connection
            dbManager = new DatabaseManager();
            if(!dbManager.initialize()){
                ConfigManager.logger("Error connecting to the database");
                return;
            }

            UserDAO userDAO = new UserDAO(dbManager);
            matchMakingSystem = new MatchMakingSystem(userDAO);

            jda = JDABuilder.createDefault(ConfigManager.getToken())
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS
                    )
                    .setActivity(Activity.playing(ConfigManager.getMessageActivity()))
                    .build();

            //Wait for the bot to be ready
            jda.awaitReady();

            //Initialize the queue listener
            queuelistener = new QueueVoiceChannelListener(jda, dbManager);
            //Add Listener to JDA
            jda.addEventListener(
                    queuelistener,
                    new CommandListener(dbManager, matchMakingSystem)
            );





        }catch (Exception e){
            ConfigManager.logger("Error starting Discord bot: " + e.getMessage());
            if(e.getMessage().contains("401")){
                ConfigManager.logger("Check your token");
            }else if(e.getMessage().contains("50001")) {
                ConfigManager.logger("Check your intents or permissions");
            }
        }
    }

    public void shutdown(){
        if(queuelistener != null){
            queuelistener.shutdown();
        }
        if(jda != null){
            jda.shutdown();
        }
        if(dbManager != null){
            dbManager.close();
        }
        ConfigManager.logger("Bot shut down");
    }

}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
