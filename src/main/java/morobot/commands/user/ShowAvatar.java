package morobot.commands.user;

import morobot.App;
import morobot.commands.Constants;
import morobot.commands.ErrorEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ShowAvatar extends ErrorEmbed {

    public void onAvatarCommand(GuildMessageReceivedEvent event, String[] args) {

        // проверяем, является ли сообщение командой .avatar.
        if (args[0].equalsIgnoreCase(App.PREFIX + "avatar")) {
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

    private void showUserAvatar(GuildMessageReceivedEvent event, String user) {
        String imageUrl;
        String memberId = findMemberId(event, user);
        Member member = null;
        if (memberId != null) {
            member = event.getGuild().getMemberById(memberId);
        }
        if (member == null) {
            errorEmbed(event, Constants.CANT_FIND_MEMBER);
        } else if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
            sendImageEmbed(event, imageUrl);
        } else {
            errorEmbed(event, Constants.NO_AVATAR);
        }
    }

    private void showSelfAvatar(GuildMessageReceivedEvent event) {
        String imageUrl;
        if ((imageUrl = event.getAuthor().getAvatarUrl()) != null) {
            sendImageEmbed(event, imageUrl);
        } else {
            errorEmbed(event, Constants.NO_SELF_AVATAR);
        }
    }

    private String findMemberId(GuildMessageReceivedEvent event, String user) {
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

    private void sendImageEmbed(GuildMessageReceivedEvent event, String imageUrL) {
        event.getMessage().delete().queue();
        EmbedBuilder image = new EmbedBuilder();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }
}
