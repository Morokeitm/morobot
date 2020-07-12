package morobot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.TimeUnit;

public class ErrorEmbed {

    protected static void errorEmbed(GuildMessageReceivedEvent event, String title, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle(title);
        error.setDescription(description);
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }

    protected static void errorEmbed(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(description);
        RestAction<Message> action = event.getChannel().sendMessage(error.build());
        action.queue((message) -> {
            //Добавляем реакцию ❌ к сообщению об ошибке
            message.addReaction("❌").queue();
            XReaction.putAndSave(message.getId(), event.getMember().getId());
            message.delete().queueAfter(15, TimeUnit.SECONDS);
        });
        error.clear();
    }
}
