package morobot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
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
        if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE) &&
                event.getReactionEmote().getName().equals("❌") &&
                !event.getUser().isBot() && usersUsedCommand.containsKey(event.getMessageId())) {
            //проверка нужна, чтобы не было краша после перезапуска программы с уже вызванной до этого командой, когда удаление будет делаться с пустой HashMap.
            deleteAndSave(event);
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
        }
        if(event.getReactionEmote().getName().equals("❌") &&
                usersUsedCommand.containsKey(event.getMessageId())) {
            if (event.getMember().getId().equals(usersUsedCommand.get(event.getMessageId()))) {
                deleteAndSave(event);
                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            } else {
                if(!event.getUser().isBot()) event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
    //Сохранение и запись событий, чтобы перезагрузка программы не нарушала логику работы с предыдущими сообщениями.
    private static void deleteAndSave(GuildMessageReactionAddEvent event) {
        usersUsedCommand.remove(event.getMessageId());
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
