package morobot.commands.user;

import morobot.commands.Constants;
import morobot.commands.CommandsStuff;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ShowAvatar extends CommandsStuff {

    public void onAvatarCommand(GuildMessageReceivedEvent event, String[] args) {

        // проверяем, является ли сообщение командой .avatar.
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

    private void showUserAvatar(GuildMessageReceivedEvent event, String user) {
        String imageUrl;
        String memberId = findMemberId(event, user);
        if (memberId == null) {
            errorEmbed(event, Constants.CANT_FIND_USER);
        } else {
            Member member = event.getGuild().getMemberById(memberId);
            if (member == null) {
                errorEmbed(event, Constants.CANT_FIND_USER);
            } else if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
                sendImageEmbed(event, imageUrl);
            } else {
                errorEmbed(event, Constants.NO_AVATAR);
            }
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

    private void sendImageEmbed(GuildMessageReceivedEvent event, String imageUrL) {
        event.getMessage().delete().queue();
        EmbedBuilder image = new EmbedBuilder();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }
}
