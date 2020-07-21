package morobot;

import morobot.command.reactions.DeleteReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class Listener extends ListenerAdapter {

    private final CommandManager cmdManager = new CommandManager();
    private final ReactionManager reactManager = new ReactionManager();
    public static GuildMessageReceivedEvent messageEvent;
    public static GuildMessageReactionAddEvent reactionEvent;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        messageEvent = event;
        User author = event.getAuthor();

        if (author.isBot() || event.isWebhookMessage()) {
            return;
        }

        String prefix = Config.get("prefix");
        String message = event.getMessage().getContentRaw();

        if (message.startsWith(prefix)) {
            cmdManager.handle(event);
        }

    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        reactionEvent = event;
        User author = event.getUser();

        if (author.isBot()) {
            return;
        }
        reactManager.handle(event);
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        String message = event.getMessageId();
        if (DeleteReaction.usersUsedCommand.containsKey(message)) {
            DeleteReaction.deleteAndSave(message);
        }
    }
}
