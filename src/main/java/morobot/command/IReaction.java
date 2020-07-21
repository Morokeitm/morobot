package morobot.command;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;

public interface IReaction {
    void reactionHandle(ReactionContext event);
    String reactionName();
    boolean hasPermission(ReactionContext event);
    default List<String> getAliases() {
        return List.of();
    }
}
