package morobot.command;

import morobot.command.reactions.DeleteReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandsStuff {

    protected static String findMemberId(CommandContext event, String user) {

        if (user.startsWith("<@")) {
            return user.startsWith("<@!") ?
                    user.replace("<@!", "").replace(">", "") :
                    user.replace("<@", "").replace(">", "");
        }
        if (!user.startsWith("<@")) {
            List<Member> members = event.getGuild().getMembersByName(user, true);
            if (members.size() > 1) {
                errorEmbed(event, Constants.TOO_MANY_MEMBERS);
                return null;
            } else if (members.size() != 0) {
                return members.get(0).getId();
            } else {
                List<Member> users = event.getGuild().getMembersByEffectiveName(user, true);
                if (users.size() > 1) {
                    errorEmbed(event, Constants.TOO_MANY_USERS);
                    return null;
                } else if (users.size() != 0) {
                    return users.get(0).getId();
                }
            }
        }
        errorEmbed(event, Constants.CANT_FIND_USER);
        return null;
    }

    protected static void errorEmbed(CommandContext event, String title, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle(event.getMember().getUser().getName() + ", " + title);
        error.setDescription(description);
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }

    protected static void errorEmbed(CommandContext event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(event.getMember().getUser().getName() + ", " + description);
        RestAction<Message> action = event.getChannel().sendMessage(error.build());
        action.queue((message) -> {
            //Добавляем реакцию ❌ к сообщению об ошибке
            message.addReaction("❌").queue();
            DeleteReaction.putAndSave(message.getId(), event.getMember().getId());
        });
        error.clear();
    }

    protected static void infoEmbed(CommandContext event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xfcba03);
        info.setDescription(description);
        event.getChannel().sendMessage(info.build())
                .delay(3, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        info.clear();
    }
}
