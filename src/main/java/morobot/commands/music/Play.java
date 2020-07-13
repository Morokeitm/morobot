package morobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.commands.Constants;
import morobot.commands.CommandsStuff;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;

public class Play extends CommandsStuff {

    private static GuildMessageReceivedEvent currentEvent;

    public void onPlayCommand(GuildMessageReceivedEvent event, String[] args) {

        currentEvent = event;
        String channelId = event.getChannel().getId();
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (event.getGuild().getAudioManager().isConnected()) {
                if (args.length == 1) {
                    if (player.isPaused()) {
                        infoEmbed(event, Constants.CONTINUE_TO_PLAY);
                        player.setPaused(false);
                    } else {
                        errorEmbed(event, Constants.NO_URL);
                    }
                } else if (args.length == 2) {
                    playSong(event, args[1], manager, player);
                }
            } else {
                errorEmbed(event, Constants.USE_JOIN_COMMAND_FIRST);
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }

    }

    private void playSong(GuildMessageReceivedEvent event, String url, PlayerManager manager, AudioPlayer player) {
        String input = String.join(" ", url);
        if (!isURL(input) && !input.startsWith("ytsearch:")) {
            errorEmbed(event, Constants.NOT_AN_URL);
        } else {
            manager.loadAndPlay(event.getChannel(), input);
            player.setVolume(10);
        }
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

    private static void addedToQueue(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder added = new EmbedBuilder();
        added.setColor(0x14f51b);
        added.setDescription(description);
        event.getChannel().sendMessage(added.build()).queue();
        added.clear();
    }

    public static void setAddedToQueue(String description) {
        addedToQueue(currentEvent, description);
    }
}
