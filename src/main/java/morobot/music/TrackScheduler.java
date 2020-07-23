package morobot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import morobot.Listener;
import morobot.command.Constants;
import morobot.command.commands.music.Play;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private static String trackEmbedId = null;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (trackEmbedId != null) {
            Listener.messageEvent
                    .getGuild()
                    .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                    .deleteMessageById(trackEmbedId)
                    .queue();
            trackEmbedId = null;
        }
        Play.getUsers().remove(0);
        Play.getUrlAdresses().remove(0);
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (trackEmbedId != null) {
            Listener.messageEvent
                    .getGuild()
                    .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                    .deleteMessageById(trackEmbedId)
                    .queue();
            trackEmbedId = null;
        }
        EmbedBuilder pause = new EmbedBuilder();
        pause.setColor(0xfcba03);
        pause.setDescription(Constants.TRACK_PAUSED);
        RestAction<Message> action = Listener.messageEvent
                .getGuild()
                .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                .sendMessage(pause.build());
        action.queue((message) -> {
            message.addReaction("▶").queue();
            message.addReaction("\uD83D\uDCCB").queue();
            message.addReaction("\uD83D\uDEAB").queue();
            trackEmbedId = message.getId();
        });
        pause.clear();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (trackEmbedId != null) {
            Listener.messageEvent
                    .getGuild()
                    .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                    .deleteMessageById(trackEmbedId)
                    .queue();
            trackEmbedId = null;
        }
        final AudioTrack track = player.getPlayingTrack();
        String min = Long.toString(track.getDuration() / 60000);
        String sec = Long.toString(track.getDuration() % 60000 / 1000);

        EmbedBuilder play = new EmbedBuilder();
        play.setColor(0x2374de);
        play.setTitle("Сейчас играет:");
        play.setDescription("[" + track.getInfo().title + "]" + "(" + Play.getUrlAdresses().get(0) + ")");
        play.addField("Продолжительность:", min + ":" + sec, true);
        play.addField("Заказал:", Play.getUsers().get(0).getAsMention(), true);
        RestAction<Message> action = Listener.messageEvent
                .getGuild()
                .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                .sendMessage(play.build());
        action.queue((message) -> {
            message.addReaction("⏸").queue();
            message.addReaction("⏩").queue();
            message.addReaction("\uD83D\uDCCB").queue();
            message.addReaction("\uD83D\uDEAB").queue();
            trackEmbedId = message.getId();
        });
        play.clear();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (trackEmbedId != null) {
            Listener.messageEvent
                    .getGuild()
                    .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                    .deleteMessageById(trackEmbedId)
                    .queue();
            trackEmbedId = null;
        }
        String min = Long.toString(track.getDuration() / 60000);
        String sec = Long.toString(track.getDuration() % 60000 / 1000);
        EmbedBuilder play = new EmbedBuilder();
        play.setColor(0x2374de);
        play.setTitle("Сейчас играет:");
        play.setDescription("[" + track.getInfo().title + "]" + "(" + Play.getUrlAdresses().get(0) + ")");
        play.addField("Продолжительность:", min + ":" + sec, true);
        play.addField("Заказал:", Play.getUsers().get(0).getAsMention(), true);
        RestAction<Message> action = Listener.messageEvent
                .getGuild()
                .getTextChannelById(Constants.MUSIC_TEXT_CHANNEL_ID)
                .sendMessage(play.build());
        action.queue((message) -> {
            message.addReaction("⏸").queue();
            message.addReaction("⏩").queue();
            message.addReaction("\uD83D\uDCCB").queue();
            message.addReaction("\uD83D\uDEAB").queue();
            trackEmbedId = message.getId();
        });
        play.clear();
    }
}