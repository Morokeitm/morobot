package morobot.command.commands.moderation;

import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class Ping extends CommandsStuff implements ICommand {
    @Override
    public void commandHandle(CommandContext event) {
        JDA jda = event.getJDA();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
            return;
        }

        jda.getRestPing().queue(
                ping -> event.getChannel()
                .sendMessageFormat("Rest ping %sms\nGateway ping: %sms", ping, jda.getGatewayPing()).queue()
        );
    }

    @Override
    public String commandName() {
        return "ping";
    }
}
