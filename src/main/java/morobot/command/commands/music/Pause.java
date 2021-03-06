package morobot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.Permission;

@Deprecated
public class Pause extends CommandsStuff implements ICommand {

    private void pauseTrack(CommandContext event, AudioPlayer player) {
        if (!event.getGuild().getAudioManager().isConnected()) {
            errorEmbed(event, Constants.NO_CONNECTION);
            return;
        }
        if (player.getPlayingTrack() == null) {
            infoEmbed(event, Constants.NO_TRACK_PLAYING);
            return;
        }
        if (player.isPaused()) {
            infoEmbed(event, Constants.TRACK_ALREADY_PAUSED);
            return;
        }
        player.setPaused(true);
    }

    @Override
    public void commandHandle(CommandContext event) {
        String channelId = event.getChannel().getId();
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;

        if (!channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            errorEmbed(event, Constants.WRONG_CHANNEL);
            return;
        }
        if (event.getArgs().size() == 0) {
            if (hasPermission(event)) {
                pauseTrack(event, player);
                return;
            }
            errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
        }
    }


    @Override
    public String commandName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return "Ставит на паузу текущий трек.\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE));
    }
}
