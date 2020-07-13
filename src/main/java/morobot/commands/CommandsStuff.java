package morobot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandsStuff {

    protected static String findMemberId(GuildMessageReceivedEvent event, String user) {
        String id = null;
        if (user.startsWith("<@")) {
            id = user.startsWith("<@!") ?
                    user.replace("<@!", "").replace(">", "") :
                    user.replace("<@", "").replace(">", "");
        } else if (!user.startsWith("<@")) {
            List<Member> members = event.getGuild().getMembersByName(user, true);
            if (members.size() > 1) {
                errorEmbed(event, Constants.TOO_MANY_MEMBERS);
            } else if (members.size() != 0) {
                id = members.get(0).getId();
            } else {
                List<Member> users = event.getGuild().getMembersByEffectiveName(user, true);
                if (users.size() > 1) {
                    errorEmbed(event, Constants.TOO_MANY_USERS);
                } else if (users.size() != 0) {
                    id = users.get(0).getId();
                }
            }
        }
        return id;
    }

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

    protected static void infoEmbed(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xfcba03);
        info.setDescription(description);
        event.getChannel().sendMessage(info.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        info.clear();
    }
}
