package morobot.command.commands.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.App;
import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Play extends CommandsStuff implements ICommand {

    private static CommandContext currentEvent;
    private final YouTube youTube;

    public Play() {
        YouTube temp = null;
        try {
            temp = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("MoroBot")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        youTube = temp;
    }

    private void playSong(CommandContext event, List<String> args, PlayerManager manager, AudioPlayer player) {
        String input = String.join(" ", args);
        if (!isURL(input)) {
            String ytSearch = searchYoutube(input);
            if (ytSearch == null) {
                errorEmbed(event, Constants.FAILED_SEARCH_RESULTS);
                return;
            }
            input = ytSearch;
        }
        manager.loadAndPlay(event.getChannel(), input);
        player.setVolume(10);
    }

    @Nullable
    private String searchYoutube(String target) {
        try {
            List<SearchResult> results = youTube.search()
                    .list("id,snippet")
                    .setQ(target)
                    .setMaxResults(1L)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(Config.get("youtube_key"))
                    .execute()
                    .getItems();
            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                return "https://www.youtube.com/watch?v=" + videoId;
            }
            errorEmbed(currentEvent, Constants.FAILED_SEARCH_RESULTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        playSong(event, event.getArgs(), manager, player);
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
