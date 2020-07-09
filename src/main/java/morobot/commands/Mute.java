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

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {

    private static boolean reaction = false;
    private static User user;
    private static Member member;
    private String errorDescription;
    private static final String MUTE_ROLE = "730486590870126623";

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            if (!event.getAuthor().isBot() && args[0].equalsIgnoreCase(App.prefix + "mute")) {
                user = event.getAuthor();
                reaction = true;
                if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                    errorDescription = "У тебя нет прав на добавление ролей.";
                    errorExceptionEmbed(event, errorDescription);
                } else if (args.length > 1 && args.length < 4) {
                    if (!args[1].startsWith("<@")) {
                        errorDescription = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]";
                        errorExceptionEmbed(event, errorDescription);
                    }
                    //Команда без счетчика времени.
                    if (args.length == 2 && args[1].startsWith("<@")) muteWithoutTimeSchedule(event, args);
                    //Команда со счетчиком времени.
                    if (args.length == 3 && args[1].startsWith("<@")) muteWithTimeSchedule(event, args);
                } else {
                    errorDescription = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]";
                    errorExceptionEmbed(event, errorDescription);
                }
            }
        }
        //Добавление реакции Х к сообщению от бота.
        addReaction(event);
    }

    private void muteWithTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        String id = args[1].startsWith("<@!") ?
                args[1].replace("<@!", "").replace(">", "") :
                args[1].replace("<@", "").replace(">", "");
        member = event.getGuild().getMemberById(id);
        if (member != null) {
            Role role = event.getGuild().getRoleById(MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                try {
                    muteWithTimeScheduleDependOnPermissions(event, member, role, args[2], id);
                } catch (NumberFormatException e) {
                    errorDescription = "Указано некорректное время отстранения\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 35791] минут.";
                    errorExceptionEmbed(event, errorDescription);
                }
            } else {
                errorDescription = member.getUser().getName() + " уже отстранен.";
                errorExceptionEmbed(event, errorDescription);
            }
        } else {
            errorDescription = "Не могу найти этого пользователя на сервере :(";
            errorExceptionEmbed(event, errorDescription);
        }
    }

    private void muteWithTimeScheduleDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String muteTime, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + member.getUser().getName();
            errorExceptionEmbed(event , errorDescription);
        } else {
            int time = Integer.parseInt(muteTime);
            if (time <= 0) {
                errorDescription = "Указано некорректное время отстранения\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 35791] минут.";
                errorExceptionEmbed(event, errorDescription);
            } else {
                if (time > 34560) {
                    errorDescription = "Указано слишком большое время отстранения.\nУкажи в диапазоне [1 - 35791] минут.";
                    errorExceptionEmbed(event, errorDescription);
                } else {
                    startTimer(event, time, role); //Таймер
                    reaction = false;
                    user = null;
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
        String id = args[1].startsWith("<@!") ?
                args[1].replace("<@!", "").replace(">", "") :
                args[1].replace("<@", "").replace(">", "");
        member = event.getGuild().getMemberById(id);
        user = event.getAuthor();
        reaction = true;
        if (member != null) {
            Role role = event.getGuild().getRoleById(MUTE_ROLE);
            if (!member.getRoles().contains(role)) {
                muteDependOnPermissions(event, member, role, id);
            } else {
                errorDescription = member.getUser().getName() + " уже отстранен.";
                errorExceptionEmbed(event, errorDescription);
            }
        } else {
            errorDescription = "Не могу найти этого пользователя на сервере :(";
            errorExceptionEmbed(event, errorDescription);
        }
    }

    private void muteDependOnPermissions(GuildMessageReceivedEvent event, Member member, Role role, String id) {
        /*Только администратор сможет отстранить любого пользователя, кроме администратора.
        Пользователь с правами ролей не сможет отстранить никого старше себя.*/
        if ((member.hasPermission(Permission.MANAGE_ROLES) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) ||
                member.hasPermission(Permission.ADMINISTRATOR)) {
            errorDescription = "Невозможно отстранить " + member.getUser().getName();
            errorExceptionEmbed(event, errorDescription);
        } else {
            reaction = false;
            user = null;
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

    private void errorExceptionEmbed(GuildMessageReceivedEvent event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(description);
        event.getChannel().sendMessage(error.build())
                .delay(15, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
        member = null;
    }

    private void addReaction(GuildMessageReceivedEvent event) {
        if (reaction && event.getMember().getUser().isBot() &&
                event.getMessage().getContentDisplay().equals("")) {
            XReaction.usersUsedCommand.put(event.getMessage().getId(), user);
            event.getMessage().addReaction("❌").queue();
            reaction = false;
            user = null;
        }
    }
}