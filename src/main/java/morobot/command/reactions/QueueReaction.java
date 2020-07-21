package morobot.command.reactions;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import morobot.command.Constants;
import morobot.command.IReaction;
import morobot.command.ReactionContext;
import morobot.command.commands.music.Play;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueReaction implements IReaction {
    @Override
    public void reactionHandle(ReactionContext event) {

        if (!event.getChannel().getId().equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            return;
        }
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        event.getReaction().removeReaction(event.getAuthor()).queue();
        viewQueueInfo(event, queue);
    }

    @Override
    public String reactionName() {
        return "\uD83D\uDCCB";
    }

    @Override
    public boolean hasPermission(ReactionContext event) {
        return true;
    }

    private static void viewQueueInfo(ReactionContext event, BlockingQueue<AudioTrack> queue) {
        int trackCount = Math.min(queue.size(), 10);
        ArrayList<AudioTrack> tracks = new ArrayList<>(queue);

        EmbedBuilder currentQueue = new EmbedBuilder();
        if (queue.isEmpty()) {
            currentQueue.setColor(0xf7f7f7);
            currentQueue.setDescription("Тут пусто ¯\\_(ツ)_/¯");
        } else {
            if (queue.size() < 3) currentQueue.setColor(0x61ff5e);
            if (queue.size() > 2 && queue.size() < 7) currentQueue.setColor(0xf7e85c);
            if (queue.size() > 6) currentQueue.setColor(0xf7685e);
            currentQueue.setTitle("Текущая очередь (всего треков: " + queue.size() + ")");
            for (int i = 0; i < trackCount; i++) {
                AudioTrack track = tracks.get(i);
                AudioTrackInfo trackInfo = track.getInfo();
                currentQueue.appendDescription((i + 1) + ") " + trackInfo.title + " [" + Play.getUsers().get(i + 1).getAsMention() + "]" + "\n\n");
            }
        }
        event.getChannel().sendMessage(currentQueue.build())
                .delay(10, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        currentQueue.clear();
    }
}
