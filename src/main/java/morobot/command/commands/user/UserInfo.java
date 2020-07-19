package morobot.command.commands.user;

import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserInfo extends CommandsStuff implements ICommand {

    private void userInfoEmbed(CommandContext event, User user, Member member) {
        ArrayList<String> roles = new ArrayList<>();
        for (Role role : member.getRoles()) {
            roles.add(role.getName());
        }

        event.getMessage().delete().queue();
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0x14f51b);
        info.setThumbnail(user.getEffectiveAvatarUrl().replaceFirst("gif", "png"));
        info.addField("Имя#Тег", String.format("%#s", user), false);
        info.addField("Имя на сервере", member.getEffectiveName(), false);
        info.addField("ID + упоминание", String.format("%s (%s)", user.getId(), member.getAsMention()), false);
        info.addField("Дата создания аккаунта: ", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true); //user.getTimeCreated().toLocalDate()
        info.addField("Присоединился к серверу: ", member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), true); //member.getTimeJoined().toLocalDate()
        info.addField("Роли на сервере: ", roles.size() == 0 ?
                "Нет" :
                roles.toString(), true);
        info.addField("Буст сервера: ", (member.getTimeBoosted() == null) ?
                "Нет" :
                "с " + member.getTimeBoosted().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        info.addField("Текущий статус", member.getOnlineStatus().name().toLowerCase().replaceAll("_", " "), true);
        info.addField("Бот: ", user.isBot() ?
                "Да" :
                "Нет", true);
        event.getChannel().sendMessage(info.build()).queue();
        info.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {

        if (event.getArgs().size() == 0) {
            userInfoEmbed(event, event.getAuthor(), event.getMember());
            return;
        }
        if (event.getArgs().size() == 1) {
            String arg = event.getArgs().get(0);
            String id = findMemberId(event, arg);
            if (id != null) {
                User user = event.getGuild().getMemberById(id).getUser();
                Member member = event.getGuild().getMemberById(id);
                if (member != null) {
                    userInfoEmbed(event, user, member);
                }
            }
        }
    }

    @Override
    public String commandName() {
        return "uinfo";
    }

    @Override
    public String getHelp() {
        return "Показывает информацию об участнике сервера.\n\n" +
                "Использование: \"" + Config.get("prefix") + this.commandName() + "\", " +
                "либо \"" + Config.get("prefix") + this.commandName() + " [имя/упоминание участника]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
