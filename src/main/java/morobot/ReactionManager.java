package morobot;

import morobot.command.IReaction;
import morobot.command.ReactionContext;
import morobot.command.reactions.NextReaction;
import morobot.command.reactions.PauseReaction;
import morobot.command.reactions.PlayReaction;
import morobot.command.reactions.DeleteReaction;
import morobot.command.reactions.QueueReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReactionManager {
    private final List<IReaction> reactions = new ArrayList<>();

    public ReactionManager() {
        addReaction(new PauseReaction());
        addReaction(new PlayReaction());
        addReaction(new NextReaction());
        addReaction(new DeleteReaction());
        addReaction(new QueueReaction());
    }

    private void addReaction(IReaction reaction) {
        boolean nameFound = this.reactions.stream()
                .anyMatch(react -> react.reactionName()
                        .equals(reaction.reactionName()));

        if (nameFound) {
            throw new IllegalArgumentException("Такая реакция уже существует!");
        }
        reactions.add(reaction);
    }

    @Nullable
    public IReaction getReaction(String search) {
        for (IReaction reaction : this.reactions) {
            if (reaction.reactionName().equals(search) || reaction.getAliases().contains(search)) {
                return reaction;
            }
        }
        return null;
    }

    void handle(GuildMessageReactionAddEvent event) {
        String reaction = event.getReactionEmote().getName();
        IReaction react = this.getReaction(reaction);

        if (react != null) {
            ReactionContext reactionContext = new ReactionContext(event, reaction);
            react.reactionHandle(reactionContext);
        }
    }
}
