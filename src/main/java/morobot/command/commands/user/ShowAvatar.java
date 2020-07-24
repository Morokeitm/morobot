package morobot.command.commands.user;

import morobot.App;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class ShowAvatar extends CommandsStuff implements ICommand {

    private void showUserAvatar(CommandContext event, String user) {
        String imageUrl;
        String memberId = findMemberId(event, user);

        if (memberId == null) {
            return;
        }
        Member member = event.getGuild().getMemberById(memberId);
        if (member == null) {
            errorEmbed(event, Constants.CANT_FIND_USER);
            return;
        }
        if ((imageUrl = member.getUser().getAvatarUrl()) != null) {
            sendImageEmbed(event, imageUrl);
            return;
        }
        errorEmbed(event, Constants.NO_AVATAR);
    }

    private void showSelfAvatar(CommandContext event) {
        String imageUrl;

        if ((imageUrl = event.getAuthor().getAvatarUrl()) != null) {
            sendImageEmbed(event, imageUrl);
            return;
        }
        errorEmbed(event, Constants.NO_SELF_AVATAR);
    }

    private void sendImageEmbed(CommandContext event, String imageUrL) {
        event.getMessage().delete().queue();
        EmbedBuilder image = new EmbedBuilder();
        image.setImage(imageUrL);
        image.setColor(0x14f51b);
        event.getChannel().sendMessage(image.build()).queue();
        image.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {
        if (event.getArgs().size() == 0) {
            showSelfAvatar(event);
            return;
        }
        if (event.getArgs().size() == 1) {
            String user = event.getArgs().get(0);
            showUserAvatar(event, user);
        }
    }

    @Override
    public String commandName() {
        return "avatar";
    }

    @Override
    public String getHelp() {
        return "Показывает картинку на аватаре участника сервера.\n\n" +
                "**Использование:** \"" + App.PREFIX + this.commandName() + "\", " +
                "либо \"" + App.PREFIX + this.commandName() + " [имя / упоминание участника]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
