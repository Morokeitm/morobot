package morobot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import morobot.commands.CommandsStuff;
import morobot.commands.Constants;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Queue extends CommandsStuff {

    public void onQueueCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                if (event.getGuild().getAudioManager().isConnected()) {
                    getCurrentQueue(event);
                } else {
                    errorEmbed(event, Constants.NO_CONNECTION);
                }
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }
    }

    private void getCurrentQueue(GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (!queue.isEmpty()) {
            viewQueueInfo(event, queue);
        } else {
            infoEmbed(event, Constants.QUEUE_IS_EMPTY);
        }
    }

    private static void viewQueueInfo(GuildMessageReceivedEvent event, BlockingQueue<AudioTrack> queue) {
        event.getMessage().delete().queue();
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
        event.getChannel().sendMessage(currentQueue.build()).queue();
        currentQueue.clear();
    }

}
