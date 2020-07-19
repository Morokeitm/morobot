package morobot.command.commands.moderation;

import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Kick extends CommandsStuff implements ICommand {

    private void kickEmbed(CommandContext event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder kick = new EmbedBuilder();
        kick.setColor(0x000000);
        kick.setDescription(description);
        event.getChannel().sendMessage(kick.build())
                .delay(25, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        kick.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {
        Message message = event.getMessage();
        Member member = event.getMember();
        List<String> args = event.getArgs();

        if(!hasPermission(event)) {
            errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
            return;
        }
        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            errorEmbed(event, Constants.WRONG_KICK_COMMAND);
            return;
        }
        Member target = message.getMentionedMembers().get(0);
        if (!member.canInteract(target) ||
                (target.hasPermission(Permission.KICK_MEMBERS) &&
                        !event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
            errorEmbed(event, Constants.NO_PERMISSION_TO_KICK);
            return;
        }
        Member selfMember = event.getSelfMember();
        if (!selfMember.canInteract(target)) {
            errorEmbed(event, Constants.CAN_NOT_KICK);
            return;
        }
        String reason = String.join(" ", args.subList(1, args.size()));
        event.getGuild()
                .kick(target, reason)
                .reason(reason)
                .queue(
                        (__) -> kickEmbed(event, target.getEffectiveName() + " исключен с сервера.\n\n" +
                                "Причина: " + reason),
                        (error) -> errorEmbed(event, "Не могу исключить. " + error.getMessage())
                );
    }

    @Override
    public String commandName() {
        return "kick";
    }

    @Override
    public String getHelp() {
        return "Выгоняет пользователя с сервера.\n\n" +
                "Использование: \"" + App.PREFIX + this.commandName() +
                " [упоминание участнника] [причина]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.KICK_MEMBERS);
    }
}
