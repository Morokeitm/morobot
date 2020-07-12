package morobot.commands.moderation;

import morobot.commands.Constants;
import morobot.commands.CommandsStuff;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearMessages extends CommandsStuff {

    public void onClearCommand(GuildMessageReceivedEvent event, String[] args) {

        if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
            if (args.length < 2) {
                errorEmbed(event, Constants.NO_COUNT_OF_MESSAGES, Constants.USAGE);
            } else {
                try {
                    deletedMessagesEmbed(event, args[1]);
                } catch (NumberFormatException e) {
                    errorEmbed(event, Constants.NOT_A_DIGIT, Constants.USAGE);
                } catch (IllegalArgumentException e) {
                    if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval") ||
                            e.toString().startsWith("java.lang.IllegalArgumentException: Must provide at least 2")) {
                        //Слишком много/мало сообщений
                        errorEmbed(event, Constants.WRONG_COUNT_OF_MESSAGES, Constants.CORRECT_COUNT_DESCRIPTION);
                    } else {
                        //Слишком старые сообщения
                        errorEmbed(event, Constants.TRY_TO_DELETE_OLD_MESSAGES, Constants.NOT_ABLE_TO_DELETE_OLD_MESSAGES);
                    }
                }
            }
        } else {
            //Нет прав для удаления сообщений
            errorEmbed(event, Constants.NO_PERMISSION_TO_DELETE_MESSAGES, Constants.WHO_ABLE_TO_USE_COMMAND);
        }
    }

    private void deletedMessagesEmbed(GuildMessageReceivedEvent event, String count) {
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
}