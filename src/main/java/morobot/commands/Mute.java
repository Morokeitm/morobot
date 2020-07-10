package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {

    private static Member member;
    private static String id;
    private String errorDescription;
    private static final String MUTE_ROLE = "730486590870126623";
    private static final String WRONG_COMMAND = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]";
    private static final String NO_PERMISSIONS = "У тебя нет прав на добавление ролей.";
    private static final String CANT_FIND_USER = "Не могу найти этого пользователя на сервере :(";
    private static final String TOO_BIG_MUTE_TIME = "Указано слишком большое время отстранения.\nУкажи в диапазоне [1 - 34560] минут.";
    private static final String WRONG_MUTE_TIME = "Указано некорректное время отстранения\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 34560] минут.";

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        User author = event.getAuthor();
        Member member = event.getMember();

        if (!author.isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            if (!author.isBot() && args[0].equalsIgnoreCase(App.prefix + "mute")) {
                if (!member.hasPermission(Permission.MANAGE_ROLES)) {
                    errorEmbed(event, NO_PERMISSIONS);
                } else if (args.length > 1 && args.length < 4) {
                    if (!args[1].startsWith("<@")) {
                        errorEmbed(event, WRONG_COMMAND);
                    }
                    //Команда без счетчика времени.
                    if (args.length == 2 && args[1].startsWith("<@")) muteWithoutTimeSchedule(event, args);
                    //Команда со счетчиком времени.
                    if (args.length == 3 && args[1].startsWith("<@")) muteWithTimeSchedule(event, args);
                } else {
                    errorEmbed(event, WRONG_COMMAND);
                }
            }
        }
    }

    private void findMemberById(GuildMessageReceivedEvent event, String arg) {
        id = arg.startsWith("<@!") ?
                arg.replace("<@!", "").replace(">", "") :
                arg.replace("<@", "").replace(">", "");
        member = event.getGuild().getMemberById(id);
    }

    private void muteWithTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        findMemberById(event, args[1]);
        if (member != null) {
            Role role = event.getGuild().getRoleById(MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                try {
                    muteWithTimeScheduleDependOnPermissions(event, member, role, args[2], id);
                } catch (NumberFormatException e) {
                    errorEmbed(event, WRONG_MUTE_TIME);
                }
            } else {
                errorDescription = member.getUser().getName() + " уже отстранен.";
                errorEmbed(event, errorDescription);
            }
        } else {
            errorEmbed(event, CANT_FIND_USER);
        }
    }

    private void muteWithTimeScheduleDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String muteTime, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + member.getUser().getName();
            errorEmbed(event , errorDescription);
        } else {
            int time = Integer.parseInt(muteTime);
            if (time <= 0) {
                errorEmbed(event, WRONG_MUTE_TIME);
            } else {
                if (time > 34560) {
                    errorEmbed(event, TOO_BIG_MUTE_TIME);
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
            Member member = Mute.member;
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
            Role role = event.getGuild().getRoleById(MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                muteDependOnPermissions(event, member, role, id);
            } else {
                errorDescription = member.getUser().getName() + " уже отстранен.";
                errorEmbed(event, errorDescription);
            }
        } else {
            errorEmbed(event, CANT_FIND_USER);
        }
    }

    private void muteDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + member.getUser().getName();
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
        succeed.setDescription(member.getUser().getName());
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
        if (time >= 60 && time <1440) {
            timeText = "Часов: " + time/60 + ", Минут: " + time%60;
        }
        if (time >= 1440) {
            timeText = "Дней: " + time/1440 + ", Часов: " + (time%1440)/60 + ", Минут: " + (time%1440)%60;
        }
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setTitle("Отстранение:");
        succeed.setDescription(member.getUser().getName());
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
        removeRole.setDescription(member.getUser().getName());
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