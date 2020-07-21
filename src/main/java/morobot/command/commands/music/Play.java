package morobot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.App;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Play extends CommandsStuff implements ICommand {

    private static CommandContext currentEvent;

    private void playSong(CommandContext event, String url, PlayerManager manager, AudioPlayer player) {
        String input = String.join(" ", url);

        if (!isURL(input) && !input.startsWith("ytsearch:")) {
            errorEmbed(event, Constants.NOT_AN_URL);
            return;
        }
        manager.loadAndPlay(event.getChannel(), input);
        player.setVolume(10);
    }

    private boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static void setErrorEmbed(String description) {
        errorEmbed(currentEvent, description);
    }

    private static void addedToQueueEmbed(CommandContext event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder added = new EmbedBuilder();
        added.setColor(0x14f51b);
        added.setDescription(description);
        event.getChannel().sendMessage(added.build())
                .delay(3, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        added.clear();
    }

    public static void setAddedToQueue(String description) {
        addedToQueueEmbed(currentEvent, description);
    }

    @Override
    public void commandHandle(CommandContext event) {
        currentEvent = event;
        String channelId = event.getChannel().getId();
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;

        if (!channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            errorEmbed(event, Constants.WRONG_CHANNEL);
            return;
        }
        if (!event.getGuild().getAudioManager().isConnected()) {
            errorEmbed(event, Constants.NO_CONNECTION);
            return;
        }
        if (event.getArgs().size() == 0) {
            if (player.isPaused()) {
                player.setPaused(false);
                return;
            }
            errorEmbed(event, Constants.NO_URL);
        }
        if (event.getArgs().size() == 1) {
            playSong(event, event.getArgs().get(0), manager, player);
        }
    }

    @Override
    public String commandName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Ставит в очередь указанный трек, либо снимает текущий трек с паузы.\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\", " +
                "либо \"" + App.PREFIX + this.commandName() + " [ссылка]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
