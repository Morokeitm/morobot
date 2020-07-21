package morobot.command.reactions;

import morobot.command.IReaction;
import morobot.command.ReactionContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DeleteReaction extends ListenerAdapter implements IReaction {

    private static final String FILE_NAME = "logs.dat";
    public static Map<String, String> usersUsedCommand = new HashMap<>();

    static {
        load();
    }

    //Сохранение и запись событий, чтобы перезагрузка программы не нарушала логику работы с предыдущими сообщениями.
    public static void deleteAndSave(String message) {
        usersUsedCommand.remove(message);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(usersUsedCommand);
            System.out.println("Лог сохраняемых id:" + DeleteReaction.usersUsedCommand);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void putAndSave(String id, String author) {
        usersUsedCommand.put(id, author);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(usersUsedCommand);
            System.out.println("Лог сохраняемых id:" + DeleteReaction.usersUsedCommand);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Map<String, String> checkMap;
            if ((checkMap = (Map<String, String>) ois.readObject()) != null) usersUsedCommand = checkMap;
            System.out.println("Лог сохраняемых id:" + DeleteReaction.usersUsedCommand);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void reactionHandle(ReactionContext event) {
        String messageId = event.getMessageId();
        User user = event.getAuthor();
        Member member = event.getMember();

        if (usersUsedCommand.containsKey(messageId)) {
            if (hasPermission(event)) {
                event.getChannel().deleteMessageById(messageId).queue();
                return;
            }
            event.getReaction().removeReaction(user).queue();
        }
    }

    @Override
    public String reactionName() {
        return "❌";
    }

    @Override
    public boolean hasPermission(ReactionContext event) {
        return (event.getMember().getId().equals(usersUsedCommand.get(event.getMessageId())) ||
               event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE));
    }
}