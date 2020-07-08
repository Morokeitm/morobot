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

    private EmbedBuilder image = new EmbedBuilder();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            // проверяем, является ли сообщение командой .avatar.
            if (args[0].equalsIgnoreCase(App.prefix + "avatar")) {
                String imageUrl;
                if (args.length == 1) {
                    /*если хочет посмотреть свой аватар.
                     проверяем есть ли аватар у пользователя*/
                    if ((imageUrl = event.getAuthor().getAvatarUrl()) != null) {
                        sendImageEmbed(event, imageUrl);
                    } else {
                        noAvatarExceptionEmbed(event);
                    }
                } else if (args.length == 2) {
                    /* если хочет посмотреть чей-то аватар.
                     проверяем, упомянут ли пользователь через @User.*/
                    if (args[1].startsWith("<@")) {
                        String id = args[1].startsWith("<@!") ?
                                args[1].replace("<@!", "").replace(">", "") :
                                args[1].replace("<@", "").replace(">", "");
                        Member member = event.getGuild().getMemberById(id);
                        if (member == null) {
                            noMemberExceptionEmbed(event);
                        } else if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
                            sendImageEmbed(event, imageUrl);
                        } else {
                            memberHasNoAvatarExceptionEmbed(event);
                        }
                    } else {
                        noMemberMentionEmbed(event);
                    }
                }
            }
        }
    }

    private void memberHasNoAvatarExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder noAvatar = new EmbedBuilder();
        noAvatar.setColor(0xf2480a);
        noAvatar.setDescription("У пользователя отсутствует картинка на аватаре.");
        event.getChannel().sendMessage(noAvatar.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        noAvatar.clear();
    }

    private void noMemberExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder noAvatar = new EmbedBuilder();
        noAvatar.setColor(0xf2480a);
        noAvatar.setDescription("Не могу найти этого пользователя на сервере :(");
        event.getChannel().sendMessage(noAvatar.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        noAvatar.clear();
    }

    private void noAvatarExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder noAvatar = new EmbedBuilder();
        noAvatar.setColor(0xf2480a);
        noAvatar.setDescription("У тебя нет картинки на аватаре, что я тебе показать должен?");
        event.getChannel().sendMessage(noAvatar.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        noAvatar.clear();
    }

    private void sendImageEmbed(GuildMessageReceivedEvent event, String imageUrL) {
        event.getMessage().delete().queue();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }

    private void noMemberMentionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder needMention = new EmbedBuilder();
        needMention.setColor(0xf2480a);
        needMention.setTitle("Нужно упомянуть пользователя");
        needMention.setDescription("Используй: " + App.prefix + "avatar @User");
        event.getChannel().sendMessage(needMention.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        needMention.clear();
    }
}
