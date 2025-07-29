package Listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.ConfigManager;

public class QueueVoiceChannelListener extends ListenerAdapter {
    private JDA jda;

    public QueueVoiceChannelListener(JDA jda) {
        this.jda = jda;
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

        //Joined the voice channel if the user is in it
        if(joinedChannel != null && joinedChannel.getId().equals(voiceChannel.getId())) {
            //Example message
            ConfigManager.logger("✅ Usuario " + event.getMember().getEffectiveName() + " se unió al canal de voz de la cola: " + voiceChannel.getName());

        }
        //Left the voice channel if the user is in it
        if(leftChannel != null && leftChannel.getId().equals(voiceChannel.getId())) {
            //Example message
            ConfigManager.logger("❌ Usuario " + event.getMember().getEffectiveName() + " abandonó el canal de voz de la cola: " + voiceChannel.getName());
        }
    }

}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
