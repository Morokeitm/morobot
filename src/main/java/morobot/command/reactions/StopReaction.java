package morobot.command.reactions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.IReaction;
import morobot.command.ReactionContext;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class StopReaction implements IReaction {
    @Override
    public void reactionHandle(ReactionContext event) {

        if (!event.getChannel().getId().equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            return;
        }
        if (!hasPermission(event)) {
            event.getReaction().removeReaction(event.getAuthor()).queue();
            return;
        }
        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(event.getGuild());
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;

        musicManager.scheduler.getQueue().clear();
        player.stopTrack();
        player.setPaused(false);
        stopAndClearQueueEmbed(event);
    }

    @Override
    public String reactionName() {
        return "\uD83D\uDEAB";
    }

    @Override
    public boolean hasPermission(ReactionContext event) {
        return (event.getMember().hasPermission(Permission.ADMINISTRATOR) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE)));
    }

    private static void stopAndClearQueueEmbed(ReactionContext event) {
        EmbedBuilder added = new EmbedBuilder();
        added.setColor(0x14f51b);
        if (!event.getGuild().getAudioManager().isConnected()) {
            added.setDescription(Constants.DELETE_QUEUE);
        } else {
            added.setDescription(Constants.STOP_MUSIC_AND_DELETE_QUEUE);
        }
        event.getChannel().sendMessage(added.build())
                .delay(3, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        added.clear();
    }
}
