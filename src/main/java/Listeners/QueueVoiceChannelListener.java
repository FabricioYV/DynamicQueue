package Listeners;

import MatchMaking.MatchMakingSystem;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.DatabaseManager;
import services.UserDAO;
import utils.ConfigManager;

import java.util.List;

public class QueueVoiceChannelListener extends ListenerAdapter {
    private JDA jda;
    private MatchMakingSystem matchMakingSystem;
    private UserDAO userDAO;


    public QueueVoiceChannelListener(JDA jda, DatabaseManager dbManager) {
        this.jda = jda;
        this.userDAO = new UserDAO(dbManager);
        this.matchMakingSystem = new MatchMakingSystem(userDAO);
    }
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        VoiceChannel voiceChannel = event.getGuild().getVoiceChannelById(ConfigManager.getQueueID());
        if (voiceChannel == null) {
            ConfigManager.logger("❌ No se encontró el canal de voz de la cola: " + ConfigManager.getQueueID());
            return;
        }
        VoiceChannel joinedChannel = event.getChannelJoined() != null ? event.getChannelJoined().asVoiceChannel() : null;
        VoiceChannel leftChannel = event.getChannelLeft() != null ? event.getChannelLeft().asVoiceChannel() : null;

        boolean queueChannelEvent = false;

        //Joined the voice channel if the user is in it
        if(joinedChannel != null && joinedChannel.getId().equals(voiceChannel.getId())) {
            //Example message
            ConfigManager.logger("✅ Usuario " + event.getMember().getEffectiveName() + " se unió al canal de voz de la cola: " + voiceChannel.getName());
            queueChannelEvent = true;
        }
        //Left the voice channel if the user is in it
        if(leftChannel != null && leftChannel.getId().equals(voiceChannel.getId())) {
            //Example message
            ConfigManager.logger("❌ Usuario " + event.getMember().getEffectiveName() + " abandonó el canal de voz de la cola: " + voiceChannel.getName());
            queueChannelEvent = true;
        }
        if(queueChannelEvent) {
            List<Member> currentMembers = voiceChannel.getMembers();

            ConfigManager.logger("Update queue with" + currentMembers.size() + " members");
            ConfigManager.logger("Current queue: " + voiceChannel.getMembers().toString());
            ConfigManager.logger("Active Matchs "+ matchMakingSystem.getActiveMatches().size());
            matchMakingSystem.updateQueue(currentMembers);
        }
    }
    public void shutdown(){
        if(matchMakingSystem != null){
            matchMakingSystem.shutdown();
        }
    }


}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
