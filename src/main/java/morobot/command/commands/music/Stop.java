package morobot.command.commands.music;

import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import morobot.music.GuildMusicManager;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Stop extends CommandsStuff implements ICommand {

    private void stopAndClearQueue(CommandContext event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);
        stopAndClearQueueEmbed(event);
    }

    private static void stopAndClearQueueEmbed(CommandContext event) {
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

    @Override
    public void commandHandle(CommandContext event) {
        String channelId = event.getChannel().getId();

        if (!channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            errorEmbed(event, Constants.WRONG_CHANNEL);
            return;
        }
        if (event.getArgs().size() == 0) {
            if (hasPermission(event)) {
                stopAndClearQueue(event);
                return;
            }
            errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
        }
    }

    @Override
    public String commandName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Останавливает текущий трек и удаляет все треки из очереди.\n\n" +
                "Использование: \"" + Config.get("prefix") + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE));
    }
}
