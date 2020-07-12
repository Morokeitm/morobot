package morobot.commands.user;

import morobot.commands.CommandsStuff;
import morobot.commands.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserInfo extends CommandsStuff {

    public void onUserInfoCommand(GuildMessageReceivedEvent event, String[] args) {

        if (args.length < 2) {
            userInfoEmbed(event, event.getAuthor(), event.getMember());
        } else if (args.length == 2) {
            String id = findMemberId(event, args[1]);
            if (id != null) {
                User user = event.getGuild().getMemberById(id).getUser();
                Member member = event.getGuild().getMemberById(id);
                if (member != null) {
                    userInfoEmbed(event, user, member);
                } else {
                    errorEmbed(event, Constants.CANT_FIND_USER);
                }
            }
        }
    }

    private void userInfoEmbed(GuildMessageReceivedEvent event, User user, Member member) {
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
        info.addField("Присоединился к серверу: ", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true); //member.getTimeJoined().toLocalDate()
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
}
