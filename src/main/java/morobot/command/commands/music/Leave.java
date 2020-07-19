package morobot.command.commands.music;

import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class Leave extends CommandsStuff implements ICommand {

    private void isConnected(CommandContext event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().closeAudioConnection();
            leaveFromMusicChannelEmbed(event);
            return;
        }
        infoEmbed(event, Constants.ALREADY_LEFT);
    }

    private void leaveFromMusicChannelEmbed(CommandContext event) {
        event.getMessage().delete().queue();
        EmbedBuilder left = new EmbedBuilder();
        left.setColor(0x14f51b);
        left.setDescription(Constants.LEFT);
        event.getChannel().sendMessage(left.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        left.clear();
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
                isConnected(event);
                return;
            }
            errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
        }
    }

    @Override
    public String commandName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "Отключает бота от канала \"music\".\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE));
    }
}
