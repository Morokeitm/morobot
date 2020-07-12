package morobot.commands.moderation;

import morobot.commands.Constants;
import morobot.commands.XReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TextMute {

    private static Member member;
    private static String id;
    private String errorDescription;

    public void onMuteCommand(GuildMessageReceivedEvent event, String[] args) {

        Member member = event.getMember();

        if (!member.hasPermission(Permission.MANAGE_ROLES)) {
            errorEmbed(event, Constants.NO_PERMISSIONS_TO_ADDING_ROLES);
        } else if (args.length > 1 && args.length < 4) {
            //Команда без счетчика времени.
            if (args.length == 2) muteWithoutTimeSchedule(event, args);
            //Команда со счетчиком времени.
            if (args.length == 3) muteWithTimeSchedule(event, args);
        } else {
            errorEmbed(event, Constants.WRONG_COMMAND);
        }
    }

    private void findMemberById(GuildMessageReceivedEvent event, String user) {
        id = null;
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
        if (id != null) {
            member = event.getGuild().getMemberById(id);
        }
    }

    private void muteWithTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        findMemberById(event, args[1]);
        if (member != null) {
            Role role = event.getGuild().getRoleById(Constants.MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                try {
                    muteWithTimeScheduleDependOnPermissions(event, member, role, args[2], id);
                } catch (NumberFormatException e) {
                    errorEmbed(event, Constants.WRONG_MUTE_TIME);
                }
            } else {
                errorDescription = member.getUser().getName() + " уже отстранен.";
                errorEmbed(event, errorDescription);
            }
        } else {
            errorEmbed(event, Constants.CANT_FIND_USER);
        }
    }

    private void muteWithTimeScheduleDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String muteTime, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + (member.getNickname() == null ?
                    member.getUser().getName() :
                    member.getUser().getName() + " (" + member.getNickname() + ")");
            errorEmbed(event, errorDescription);
        } else {
            int time = Integer.parseInt(muteTime);
            if (time <= 0) {
                errorEmbed(event, Constants.WRONG_MUTE_TIME);
            } else {
                if (time > 34560) {
                    errorEmbed(event, Constants.TOO_BIG_MUTE_TIME);
                } else {
                    startTimer(event, time, role); //Таймер
                    event.getGuild().addRoleToMember(id, role).queue(); //Добавляем роль мута
                    roleAddEmbed(event, time); //Сообщение об отстранении пользователя
                }
            }
        }
    }

    private void startTimer(GuildMessageReceivedEvent event, int time, Role role) {
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

    private void muteWithoutTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        findMemberById(event, args[1]);
//        XReaction.user = event.getAuthor();
//        XReaction.reaction = true;
        if (member != null) {
            Role role = event.getGuild().getRoleById(Constants.MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                muteDependOnPermissions(event, member, role, id);
            } else {
                errorDescription = member.getNickname() == null ?
                        member.getUser().getName() + " уже отстранен." :
                        member.getUser().getName() + " (" + member.getNickname() + ")" + " уже отстранен.";
                errorEmbed(event, errorDescription);
            }
        } else {
            errorEmbed(event, Constants.CANT_FIND_USER);
        }
    }

    private void muteDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + (member.getNickname() == null ?
                    member.getUser().getName() :
                    member.getUser().getName() + " (" + member.getNickname() + ")");
            errorEmbed(event, errorDescription);
        } else {
            event.getGuild().addRoleToMember(id, role).queue(); //Добавляем роль мута
            roleAddEmbed(event); //Сообщение об отстранении пользователя
        }
    }

    private void roleAddEmbed(GuildMessageReceivedEvent event) {
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

    private void roleAddEmbed(GuildMessageReceivedEvent event, int time) {
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

    private void roleRemovedEmbed(GuildMessageReceivedEvent event, Member member) {
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

    private void errorEmbed(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(description);
        RestAction<Message> action = event.getChannel().sendMessage(error.build());
        action.queue((message) -> {
            //Добавляем реакцию ❌ к сообщению об ошибке
            message.addReaction("❌").queue();
            XReaction.putAndSave(message.getId(), event.getMember().getId());
            message.delete().queueAfter(15, TimeUnit.SECONDS);
        });
        error.clear();
        member = null;
    }
}