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
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args[0].equalsIgnoreCase(App.prefix + "clear")) {
            if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
                if (args.length < 2) {
                    noCountOfMessagesException(event);
                } else {
                    try {
                        deleteMessages(event, args[1]);
                    } catch (NumberFormatException e) {
                        notNumberException(event);
                    } catch (IllegalArgumentException e) {
                        if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval") ||
                                e.toString().startsWith("java.lang.IllegalArgumentException: Must provide at least 2")) {
                            //Слишком много/мало сообщений
                            illegalCountOfMessagesException(event);
                        } else {
                            //Слишком старые сообщения
                            tooOldMessageException(event);
                        }
                    }
                }
            } else {
                noPermissionsToDeleteException(event);
            }
        }
    }

    private void notNumberException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder usage = new EmbedBuilder();
        usage.setColor(0xf2480a);
        usage.setTitle("Указанное значение не является числом.");
        usage.setDescription("Используй: " + App.prefix + "clear [1-99]");
        event.getChannel().sendMessage(usage.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        usage.clear();
    }

    private void noCountOfMessagesException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder usage = new EmbedBuilder();
        usage.setColor(0xf2480a);
        usage.setTitle("Не указано количество удаляемых сообщений.");
        usage.setDescription("Используй: " + App.prefix + "clear [1-99]");
        event.getChannel().sendMessage(usage.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        usage.clear();
    }
    private void deleteMessages(GuildMessageReceivedEvent event, String count) {
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
    private void illegalCountOfMessagesException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle("Указано недопустимое количество сообщений.");
        error.setDescription("Возможное количество: 1-99.");
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }
    private void tooOldMessageException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle("Попытка удалить старые сообщения.");
        error.setDescription("Нельзя удалить сообщения, оставленные более двух недель назад.");
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }
    private void noPermissionsToDeleteException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle("У вас нет прав на удаление сообщений.");
        error.setDescription("Команда доступна пользователям с правами удаления сообщений");
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }
}