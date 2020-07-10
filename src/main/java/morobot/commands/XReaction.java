package morobot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XReaction extends ListenerAdapter {

    public static Map<String, String> usersUsedCommand = new HashMap<>();
    static {
        load();
    }
    //Если пользователь, который ввел команду, или пользователь с правами удаления сообщений нажимает на реакцию, то сообщение бота удаляется.
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {

        String messageId = event.getMessageId();
        String reaction = event.getReactionEmote().getName();
        User user = event.getUser();
        Member member = event.getMember();

        if(reaction.equals("❌") &&
                !user.isBot() &&
                usersUsedCommand.containsKey(messageId)) {
            if (member.getPermissions().contains(Permission.MESSAGE_MANAGE)) {
                event.getChannel().deleteMessageById(messageId).queue();
            } else if (member.getId().equals(usersUsedCommand.get(messageId))) {
                event.getChannel().deleteMessageById(messageId).queue();
            } else if(!user.isBot()) {
                event.getReaction().removeReaction(user).queue();
            }
        }
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        String message = event.getMessageId();
        if (usersUsedCommand.containsKey(message)) {
            deleteAndSave(message);
        }
    }

    //Сохранение и запись событий, чтобы перезагрузка программы не нарушала логику работы с предыдущими сообщениями.
    private static void deleteAndSave(String message) {
        usersUsedCommand.remove(message);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("logs.dat"))) {
            oos.writeObject(usersUsedCommand);
            System.out.println(XReaction.usersUsedCommand);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void putAndSave (String id, String author) {
        usersUsedCommand.put(id, author);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("logs.dat"))) {
            oos.writeObject(usersUsedCommand);
            System.out.println(XReaction.usersUsedCommand);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void load () {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("logs.dat"))) {
            Map<String, String> checkMap;
            if ((checkMap = (Map<String, String>) ois.readObject()) != null) {
                usersUsedCommand = checkMap;
            }
            System.out.println(XReaction.usersUsedCommand);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
