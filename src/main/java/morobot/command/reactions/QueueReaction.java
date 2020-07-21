package morobot.command.reactions;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import morobot.command.Constants;
import morobot.command.IReaction;
import morobot.command.ReactionContext;
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

        if (queue.isEmpty()) {
            event.getReaction().removeReaction(event.getAuthor()).queue();
            return;
        }
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
        currentQueue.setColor(0x2374de);
        currentQueue.setTitle("Текущая очередь (всего треков: " + queue.size() + ")");
        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo trackInfo = track.getInfo();
            currentQueue.appendDescription((i + 1) + ") " + trackInfo.title + "\n\n");
        }
        event.getChannel().sendMessage(currentQueue.build())
                .delay(10, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        currentQueue.clear();
    }
}
