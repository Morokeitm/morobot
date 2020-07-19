package morobot.command.commands.moderation;

import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearMessages extends CommandsStuff implements ICommand {

    private void deletedMessagesEmbed(CommandContext event, String count) {
        List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(count) + 1).complete();
        event.getChannel().deleteMessages(messages).queue();
        EmbedBuilder complete = new EmbedBuilder();
        complete.setColor(0x14f51b);
        complete.setTitle("Сообщения удалены.");
        complete.setDescription("Удаленных сообщений: " + (Integer.parseInt(count)));
        event.getChannel().sendMessage(complete.build())
                .delay(2, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        complete.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {
        if (!event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
            errorEmbed(event, Constants.NO_PERMISSION_TO_DELETE_MESSAGES, Constants.WHO_ABLE_TO_USE_COMMAND);
            return;
        }
        if (event.getArgs().size() == 0) {
            errorEmbed(event, Constants.NO_COUNT_OF_MESSAGES, Constants.USAGE);
            return;
        }
        try {
            deletedMessagesEmbed(event, event.getArgs().get(0));
        } catch (NumberFormatException e) {
            errorEmbed(event, Constants.NOT_A_DIGIT, Constants.USAGE);
        } catch (IllegalArgumentException e) {
            if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval") ||
                    e.toString().startsWith("java.lang.IllegalArgumentException: Must provide at least 2")) {
                //Слишком много/мало сообщений
                errorEmbed(event, Constants.WRONG_COUNT_OF_MESSAGES, Constants.CORRECT_COUNT_DESCRIPTION);
                return;
            }
            //Слишком старые сообщения
            errorEmbed(event, Constants.TRY_TO_DELETE_OLD_MESSAGES, Constants.NOT_ABLE_TO_DELETE_OLD_MESSAGES);
        }
    }


    @Override
    public String commandName() {
        return "clear";
    }

    @Override
    public String getHelp() {
        return "Удаляет указанное количество последних сообщений.\n\n" +
                "Использование: \"" + Config.get("prefix") + this.commandName() + " [1-99]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MESSAGE_MANAGE);
    }
}