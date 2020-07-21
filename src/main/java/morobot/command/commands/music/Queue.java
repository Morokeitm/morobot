package morobot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Deprecated
public class Queue extends CommandsStuff implements ICommand {

    private void getCurrentQueue(CommandContext event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (!queue.isEmpty()) {
            viewQueueInfo(event, queue);
            return;
        }
        infoEmbed(event, Constants.QUEUE_IS_EMPTY);
    }

    private static void viewQueueInfo(CommandContext event, BlockingQueue<AudioTrack> queue) {
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
        event.getChannel().sendMessage(currentQueue.build())
                .delay(25, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        currentQueue.clear();
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
                getCurrentQueue(event);
                return;
            }
            errorEmbed(event, Constants.NO_CONNECTION);
        }
    }

    @Override
    public String commandName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Показывает текущую очередь треков.\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
