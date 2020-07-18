package morobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.commands.CommandsStuff;
import morobot.commands.Constants;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Pause extends CommandsStuff {

    public void onPauseCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                        event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE))) {
                    pauseTrack(event, player);
                } else {
                    errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
                }
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }
    }

    private void pauseTrack(GuildMessageReceivedEvent event, AudioPlayer player) {
        if (!event.getGuild().getAudioManager().isConnected()) {
            errorEmbed(event, Constants.NO_CONNECTION);
        } else if (player.getPlayingTrack() == null) {
            infoEmbed(event, Constants.NO_TRACK_PLAYING);
        } else if (player.isPaused()) {
            infoEmbed(event, Constants.TRACK_ALREADY_PAUSED);
        } else {
            player.setPaused(true);
            infoEmbed(event, Constants.TRACK_PAUSED);
        }
    }
}
