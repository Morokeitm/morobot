package morobot.command.commands.moderation;

import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.Constants;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TextMute extends CommandsStuff implements ICommand {

    private static Member member;
    private static String id;
    private String errorDescription;

    private void muteWithTimeSchedule(CommandContext event, List<String> args) {
        findMemberById(event, args.get(0));
        if (member != null) {

            Role role = event.getGuild().getRoleById(Constants.MUTE_ROLE);

            if (!member.getRoles().contains(role)) {
                try {
                    muteWithTimeScheduleDependOnPermissions(event, member, role, args.get(1), id);
                } catch (NumberFormatException e) {
                    errorEmbed(event, Constants.WRONG_MUTE_TIME);
                }
                return;
            }
            errorDescription = member.getUser().getName() + " уже отстранен.";
            errorEmbed(event, errorDescription);
        }
    }

    private void muteWithTimeScheduleDependOnPermissions(CommandContext event, Member member, Role role, String muteTime, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) &&
                !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "невозможно отстранить " + (member.getNickname() == null ?
                    member.getUser().getName() :
                    member.getUser().getName() + " (" + member.getNickname() + ")");
            errorEmbed(event, errorDescription);
            return;
        }
        int time = Integer.parseInt(muteTime);
        if (time <= 0) {
            errorEmbed(event, Constants.WRONG_MUTE_TIME);
            return;
        }
        if (time > 34560) {
            errorEmbed(event, Constants.TOO_BIG_MUTE_TIME);
            return;
        }
        startTimer(event, time, role); //Таймер
        event.getGuild().addRoleToMember(id, role).queue(); //Добавляем роль мута
        roleAddEmbed(event, time); //Сообщение об отстранении пользователя
    }

    private void startTimer(CommandContext event, int time, Role role) {
        new Timer().schedule(new TimerTask() {
            Member member = TextMute.member;

            @Override
            public void run() {
                if (member.getRoles().contains(role)) {
                    event.getGuild().removeRoleFromMember(member, role).queue();
                    roleRemovedEmbed(event, member);
                }
            }
        }, time * 60000); //Время отстранения указывается в минутах
    }

    private void muteWithoutTimeSchedule(CommandContext event, List<String> args) {
        findMemberById(event, args.get(0));
        if (member != null) {
            Role role = event.getGuild().getRoleById(Constants.MUTE_ROLE);

            if (!member.getRoles().contains(role)) {
                muteDependOnPermissions(event, member, role, id);
                return;
            }
            errorDescription = member.getNickname() == null ?
                    member.getUser().getName() + " уже отстранен." :
                    member.getUser().getName() + " (" + member.getNickname() + ")" + " уже отстранен.";
            errorEmbed(event, errorDescription);
        }
    }

    private void muteDependOnPermissions(CommandContext event, Member member, Role role, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) &&
                !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + (member.getNickname() == null ?
                    member.getUser().getName() :
                    member.getUser().getName() + " (" + member.getNickname() + ")");
            errorEmbed(event, errorDescription);
            return;
        }
        event.getGuild().addRoleToMember(id, role).queue(); //Добавляем роль мута
        roleAddEmbed(event); //Сообщение об отстранении пользователя
    }

    private void findMemberById(CommandContext event, String user) {
        id = findMemberId(event, user);
        if (id == null) {
            member = null;
            return;
        }
        member = event.getGuild().getMemberById(id);
    }

    private void roleAddEmbed(CommandContext event) {
        event.getMessage().delete().queue();
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setTitle("Отстранение:");
        succeed.setDescription(member.getNickname() == null ?
                member.getUser().getName() :
                member.getUser().getName() + " (" + member.getNickname() + ")");
        event.getChannel().sendMessage(succeed.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        succeed.clear();
        member = null;
    }

    private void roleAddEmbed(CommandContext event, int time) {
        event.getMessage().delete().queue();
        String timeText = "";
        if (time < 60) timeText = "Минут: " + time;
        if (time >= 60 && time < 1440) {
            timeText = "Часов: " + time / 60 + ", Минут: " + time % 60;
        }
        if (time >= 1440) {
            timeText = "Дней: " + time / 1440 + ", Часов: " + (time % 1440) / 60 + ", Минут: " + (time % 1440) % 60;
        }
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setTitle("Отстранение:");
        succeed.setDescription(member.getNickname() == null ?
                member.getUser().getName() :
                member.getUser().getName() + " (" + member.getNickname() + ")");
        succeed.addField("Время:", timeText, true);
        event.getChannel().sendMessage(succeed.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        succeed.clear();
        member = null;
    }

    private void roleRemovedEmbed(CommandContext event, Member member) {
        EmbedBuilder removeRole = new EmbedBuilder();
        removeRole.setColor(0x14f51b);
        removeRole.setTitle("Отстранение снято:");
        removeRole.setDescription(member.getNickname() == null ?
                member.getUser().getName() :
                member.getUser().getName() + " (" + member.getNickname() + ")");
        event.getChannel().sendMessage(removeRole.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        removeRole.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {

        if (!hasPermission(event)) {
            errorEmbed(event, Constants.NO_PERMISSIONS_TO_ADDING_ROLES);
            return;
        }
        if (event.getArgs().size() > 0 && event.getArgs().size() < 3) {
            //Команда без счетчика времени.
            if (event.getArgs().size() == 1) muteWithoutTimeSchedule(event, event.getArgs());
            //Команда со счетчиком времени.
            if (event.getArgs().size() == 2) muteWithTimeSchedule(event, event.getArgs());
            return;
        }
        errorEmbed(event, Constants.WRONG_COMMAND);
    }

    @Override
    public String commandName() {
        return "mute";
    }

    @Override
    public String getHelp() {
        return "Отстраняет указанного участника сервера, выдавая ему роль мута.\n\n" +
                "Использование: \"" + Config.get("prefix") + this.commandName() + " [user]\", " +
                "либо \"" + Config.get("prefix") + this.commandName() + " [имя/упоминание участника] [время(мин)]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return event.getMember().hasPermission(Permission.MANAGE_ROLES);
    }
}