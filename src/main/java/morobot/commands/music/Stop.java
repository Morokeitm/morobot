package morobot.commands.music;

import morobot.commands.Constants;
import morobot.commands.CommandsStuff;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Stop extends CommandsStuff {

    public void onStopCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE)) ||
                        event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    stopAndClearQueue(event);
                } else {
                    errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
                }
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }
    }


    private void stopAndClearQueue(GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);
        stopAndClearQueueEmbed(event);
    }

    private static void stopAndClearQueueEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder added = new EmbedBuilder();
        added.setColor(0x14f51b);
        if (!event.getGuild().getAudioManager().isConnected()) {
            added.setDescription(Constants.DELETE_QUEUE);
        } else {
            added.setDescription(Constants.STOP_MUSIC_AND_DELETE_QUEUE);
        }
        event.getChannel().sendMessage(added.build()).queue();
        added.clear();
    }
}
