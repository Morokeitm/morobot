package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearMessages extends ListenerAdapter {

    private static final String USAGE = "Используй: " + App.prefix + "clear [1-99]";
    private static final String NOT_A_DIGIT = "Указанное значение не является числом.";
    private static final String NO_COUNT_OF_MESSAGES = "Не указано количество удаляемых сообщений.";
    private static final String WRONG_COUNT_OF_MESSAGES = "Указано недопустимое количество сообщений.";
    private static final String WHO_ABLE_TO_USE_COMMAND = "Команда доступна только пользователям с правами удаления сообщений";
    private static final String CORRECT_COUNT_DESCRIPTION = "Возможное количество: 1-99.";
    private static final String TRY_TO_DELETE_OLD_MESSAGES = "Попытка удалить старые сообщения.";
    private static final String NOT_ABLE_TO_DELETE_OLD_MESSAGES = "Нельзя удалить сообщения, оставленные более двух недель назад.";
    private static final String NO_PERMISSION_TO_DELETE_MESSAGES = "Нет прав на удаление сообщений.";

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args[0].equalsIgnoreCase(App.prefix + "clear")) {
            if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
                if (args.length < 2) {
                    errorEmbed(event, NO_COUNT_OF_MESSAGES, USAGE);
                } else {
                    try {
                        deletedMessagesEmbed(event, args[1]);
                    } catch (NumberFormatException e) {
                        errorEmbed(event, NOT_A_DIGIT, USAGE);
                    } catch (IllegalArgumentException e) {
                        if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval") ||
                                e.toString().startsWith("java.lang.IllegalArgumentException: Must provide at least 2")) {
                            //Слишком много/мало сообщений
                            errorEmbed(event, WRONG_COUNT_OF_MESSAGES, CORRECT_COUNT_DESCRIPTION);
                        } else {
                            //Слишком старые сообщения
                            errorEmbed(event, TRY_TO_DELETE_OLD_MESSAGES, NOT_ABLE_TO_DELETE_OLD_MESSAGES);
                        }
                    }
                }
            } else {
                //Нет прав для удаления сообщений
                errorEmbed(event, NO_PERMISSION_TO_DELETE_MESSAGES, WHO_ABLE_TO_USE_COMMAND);
            }
        }
    }

    private void errorEmbed(GuildMessageReceivedEvent event, String title, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder usage = new EmbedBuilder();
        usage.setColor(0xf2480a);
        usage.setTitle(title);
        usage.setDescription(description);
        event.getChannel().sendMessage(usage.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        usage.clear();
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