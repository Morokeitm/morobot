package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class ShowAvatar extends ListenerAdapter {

    private static final String NO_AVATAR = "У пользователя отсутствует картинка на аватаре.";
    private static final String CANT_FIND_MEMBER = "Не могу найти этого пользователя на сервере :(";
    private static final String NO_SELF_AVATAR = "У тебя нет картинки на аватаре, что я тебе показать должен?";
    private static final String USAGE = "Используй: " + App.prefix + "avatar @User";
    private static final String NEED_TO_MENTION= "Нужно упомянуть пользователя";

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            // проверяем, является ли сообщение командой .avatar.
            if (args[0].equalsIgnoreCase(App.prefix + "avatar")) {
                if (args.length == 1) {
                    /*если хочет посмотреть свой аватар.
                     проверяем есть ли аватар у пользователя*/
                    showSelfAvatar(event);
                } else if (args.length == 2) {
                    /* если хочет посмотреть чей-то аватар.
                     проверяем, упомянут ли пользователь через @User.*/
                    showUserAvatar(event, args[1]);
                }
            }
        }
    }

    private void showUserAvatar(GuildMessageReceivedEvent event, String user) {
        String imageUrl;
        if (user.startsWith("<@")) {
            Member member = findMember(event, user);
            if (member == null) {
                errorEmbed(event, CANT_FIND_MEMBER, null);
            } else if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
                sendImageEmbed(event, imageUrl);
            } else {
                errorEmbed(event, NO_AVATAR, null);
            }
        } else {
            errorEmbed(event, NEED_TO_MENTION, USAGE);
        }
    }

    private void showSelfAvatar(GuildMessageReceivedEvent event) {
        String imageUrl;
        if ((imageUrl = event.getAuthor().getAvatarUrl()) != null) {
            sendImageEmbed(event, imageUrl);
        } else {
            errorEmbed(event, NO_SELF_AVATAR, null);
        }
    }

    private Member findMember(GuildMessageReceivedEvent event, String user) {
        String id = user.startsWith("<@!") ?
                user.replace("<@!", "").replace(">", "") :
                user.replace("<@", "").replace(">", "");
        return event.getGuild().getMemberById(id);
    }

    private void errorEmbed(GuildMessageReceivedEvent event, String title, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(title);
        if (description != null) error.setDescription(description);
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
    }

    private void sendImageEmbed(GuildMessageReceivedEvent event, String imageUrL) {
        event.getMessage().delete().queue();
        EmbedBuilder image = new EmbedBuilder();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }
}
