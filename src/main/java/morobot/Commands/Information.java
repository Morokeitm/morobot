package morobot.Commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Information extends ListenerAdapter {

    private static boolean reaction = false;
    private static User user;
    private static Map <String, User> users = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (!event.getAuthor().isBot()) {
            if (args[0].equalsIgnoreCase(App.prefix + "info")) {
                user = event.getAuthor();
                reaction = true;
                event.getMessage().delete().queue();
                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("~ Информация о боте ~");
                info.setDescription("Для тебя доступны следующие команды:");
                info.addField(App.prefix + "AVATAR [USER]",
                        "Я покажу аватар пользователя, имя которого ты укажешь.\n" +
                                "Если никого не укажешь, то сможешь полюбоваться на свой аватар.", false);
                if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
                    info.addField(App.prefix + "CLEAR [1-99]",
                            "Написали много лишнего? Я удалю эти сообщения!\n" +
                                    "Имей в виду, что за один раз я смогу удалить не больше 99 сообщений.", false);
                }
                info.setFooter("Developed by Morokei_tm", "https://cdn.discordapp.com/avatars/319137115139080192/27b8ae9889feb379950af141841d48b4.png");
                info.setColor(0x2374de);
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            }
        }
        //Добавление реакции Х к сообщению от бота.
        if (event.getMember().getUser().isBot() &&
                event.getMessage().getContentDisplay().equals("") && reaction) {
            users.put(event.getMessage().getId(), user);
            event.getMessage().addReaction("❌").queue();
            reaction = false;
            user = null;
        }
    }
    //Если пользователь, который ввел команду, или пользователь с правами удаления сообщений нажимает на реакцию, то сообщение бота удаляется.
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE) &&
                event.getReactionEmote().getName().equals("❌") &&
                !event.getUser().isBot()) {
            //проверка нужна, чтобы не было краша после перезапуска программы с уже вызванной до этого командой, когда удаление будет делаться с пустой HashMap.
            if(users.containsKey(event.getMessageId())) users.remove(event.getMessageId());
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
        }
        if(event.getReactionEmote().getName().equals("❌") &&
                users.containsKey(event.getMessageId())) {
            if (event.getUser().equals(users.get(event.getMessageId()))) {
                users.remove(event.getMessageId());
                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            }
        }
    }
}
