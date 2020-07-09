package morobot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class XReaction extends ListenerAdapter {

    static Map<String, User> usersUsedCommand = new HashMap<>();

    //Если пользователь, который ввел команду, или пользователь с правами удаления сообщений нажимает на реакцию, то сообщение бота удаляется.
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE) &&
                event.getReactionEmote().getName().equals("❌") &&
                !event.getUser().isBot()) {
            //проверка нужна, чтобы не было краша после перезапуска программы с уже вызванной до этого командой, когда удаление будет делаться с пустой HashMap.
            if(usersUsedCommand.containsKey(event.getMessageId())) usersUsedCommand.remove(event.getMessageId());
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
        }
        if(event.getReactionEmote().getName().equals("❌") &&
                usersUsedCommand.containsKey(event.getMessageId())) {
            if (event.getUser().equals(usersUsedCommand.get(event.getMessageId()))) {
                usersUsedCommand.remove(event.getMessageId());
                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            } else {
                if(!event.getUser().isBot()) event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
