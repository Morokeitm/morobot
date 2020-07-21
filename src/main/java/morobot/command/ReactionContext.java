package morobot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;


public class ReactionContext {
    private final GuildMessageReactionAddEvent event;
    private final String reaction;

    public ReactionContext(GuildMessageReactionAddEvent event, String reaction) {
        this.event = event;
        this.reaction = reaction;
    }

    public Guild getGuild() {
        return this.event.getGuild();
    }

    public GuildMessageReactionAddEvent getEvent() {
        return this.event;
    }

    public String getMessageId() {
        return event.getMessageId();
    }

    public Member getMember() {
        return event.getMember();
    }

    public User getAuthor() {
        return event.getUser();
    }

    public TextChannel getChannel() {
        return event.getChannel();
    }

    public MessageReaction getReaction() {
        return event.getReaction();
    }
}
