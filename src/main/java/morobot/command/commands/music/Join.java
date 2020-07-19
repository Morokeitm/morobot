package morobot.command.commands.music;

import morobot.App;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class Join extends CommandsStuff implements ICommand {

    private void isConnected(CommandContext event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            infoEmbed(event, Constants.ALREADY_JOINED);
            return;
        }
        event.getGuild()
                .getAudioManager()
                .openAudioConnection(event.getGuild()
                        .getVoiceChannelById(Constants.MUSIC_CHANNEL_ID));
        joinedToMusicChannelEmbed(event);
    }

    private void joinedToMusicChannelEmbed(CommandContext event) {
        event.getMessage().delete().queue();
        EmbedBuilder joined = new EmbedBuilder();
        joined.setColor(0x14f51b);
        joined.setDescription(Constants.JOINED);
        event.getChannel().sendMessage(joined.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        joined.clear();
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
        return "join";
    }

    @Override
    public String getHelp() {
        return "Подключает бота к каналу \"music\".\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + "\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE));
    }
}
