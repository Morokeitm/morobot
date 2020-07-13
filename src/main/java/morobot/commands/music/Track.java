package morobot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import morobot.commands.CommandsStuff;
import morobot.commands.Constants;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Track extends CommandsStuff {

    public void onTrackCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                if (event.getGuild().getAudioManager().isConnected()) {
                    getCurrentTrack(event);
                } else {
                    errorEmbed(event, Constants.NO_CONNECTION);
                }
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }
    }

    private void getCurrentTrack(GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        AudioTrack track = musicManager.player.getPlayingTrack();
        if (track != null) {
            viewTrackInfo(event, track);
        } else {
            infoEmbed(event, Constants.NO_TRACK_TO_VIEW);
        }
    }

    private static void viewTrackInfo(GuildMessageReceivedEvent event, AudioTrack track) {
        event.getMessage().delete().queue();
        String min = Long.toString(track.getDuration() / 60000);
        String sec = Long.toString(track.getDuration() % 60000 / 1000);

        EmbedBuilder added = new EmbedBuilder();
        added.setColor(0x2374de);
        added.addField("Сейчас играет:", track.getInfo().title, false);
        added.addField("Продолжительность:", min + ":" + sec, false);
        event.getChannel().sendMessage(added.build()).queue();
        added.clear();
    }
}
