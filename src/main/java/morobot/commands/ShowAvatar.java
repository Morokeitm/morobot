package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class ShowAvatar extends ListenerAdapter {

    private EmbedBuilder image = new EmbedBuilder();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            // определяем сколько слов содержит сообщение.
            String[] args = event.getMessage().getContentRaw().split(" ");
            // проверяем, является ли сообщение командой .avatar.
            if (args[0].equalsIgnoreCase(App.prefix + "avatar")) {
                String imageUrl;
                if (args.length == 1) {
                    // если хочет посмотреть свой аватар.
                    // проверяем есть ли аватар у пользователя
                    if ((imageUrl = event.getAuthor().getAvatarUrl()) != null) {
                        sendImage(event, imageUrl);
                    } else {
                        event.getChannel().sendMessage("У тебя нет картинки на аватаре, что я тебе показать должен?").queue();
                    }
                } else if (args.length == 2) {
                    // если хочет посмотреть чей-то аватар.
                    // проверяем, упомянут ли пользователь через @User.
                    if (args[1].startsWith("<@")) {
                        String id = args[1].startsWith("<@!") ?
                                args[1].replace("<@!", "").replace(">", "") :
                                args[1].replace("<@", "").replace(">", "");
                        Member member = event.getGuild().getMemberById(id);
                        if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
                            sendImage(event, imageUrl);
                        } else {
                            event.getChannel().sendMessage("У этого пользователя нет картинки на аватаре.").queue();
                        }
                    } else {
                        sendError(event);
                    }
                }
            }
        }
    }
    private void sendImage(MessageReceivedEvent event, String imageUrL) {
        event.getMessage().delete().queue();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }
    private void sendError(MessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder needMention = new EmbedBuilder();
        needMention.setColor(0xf2480a);
        needMention.setTitle("Нужно упомянуть пользователя");
        needMention.setDescription("Напиши команду вот так: " + App.prefix + "avatar @User");
        event.getChannel().sendMessage(needMention.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        needMention.clear();
    }
}
