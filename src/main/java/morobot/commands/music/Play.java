package morobot.commands.music;

import morobot.commands.Constants;
import morobot.commands.XReaction;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Play {

    private static GuildMessageReceivedEvent currentEvent;

    public void onPlayCommand(GuildMessageReceivedEvent event, String[] args) {

        currentEvent = event;
        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                errorEmbed(event, Constants.NO_URL);
            } else if (args.length == 2) {
                playSong(event, args[1]);
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }

    }

    private void playSong(GuildMessageReceivedEvent event, String url) {
        if (event.getGuild().getAudioManager().isConnected()) {
            String input = String.join(" ", url);
            if (!isURL(input) && !input.startsWith("ytsearch:")) {
                errorEmbed(event, Constants.NOT_AN_URL);
            } else {
                PlayerManager manager = PlayerManager.getInstance();
                manager.loadAndPlay(event.getChannel(), input);
                manager.getGuildMusicManager(event.getGuild()).player.setVolume(10);
            }
        } else {
            errorEmbed(event, Constants.USE_JOIN_COMMAND_FIRST);
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

    private static void errorEmbed(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(description);
        RestAction<Message> action = event.getChannel().sendMessage(error.build());
        action.queue((message) -> {
            //Добавляем реакцию ❌ к сообщению об ошибке
            message.addReaction("❌").queue();
            XReaction.putAndSave(message.getId(), event.getMember().getId());
            message.delete().queueAfter(15, TimeUnit.SECONDS);
        });
        error.clear();
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
