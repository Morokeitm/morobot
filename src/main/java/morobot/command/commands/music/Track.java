package morobot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

@Deprecated
public class Track extends CommandsStuff implements ICommand {

    private void getCurrentTrack(CommandContext event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        AudioTrack track = musicManager.player.getPlayingTrack();

        if (track != null) {
            viewTrackInfoEmbed(event, track);
            return;
        }
        infoEmbed(event, Constants.NO_TRACK_PLAYING);
    }

    private static void viewTrackInfoEmbed(CommandContext event, AudioTrack track) {
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

    @Override
    public void commandHandle(CommandContext event) {
        String channelId = event.getChannel().getId();

        if (!channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            errorEmbed(event, Constants.WRONG_CHANNEL);
            return;
        }
        if (event.getArgs().size() == 0) {
            if (event.getGuild().getAudioManager().isConnected()) {
                getCurrentTrack(event);
                return;
            }
            errorEmbed(event, Constants.NO_CONNECTION);
        }
    }

    @Override
    public String commandName() {
        return "track";
    }

    @Override
    public String getHelp() {
        return "Показывет информацию о текущем треке.\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
