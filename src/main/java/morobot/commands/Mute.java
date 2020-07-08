package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {

    private static Member member;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            if (!event.getAuthor().isBot() && args[0].equalsIgnoreCase(App.prefix + "mute")) {
                //Команда без счетчика времени.
                if (args.length == 2 && args[1].startsWith("<@")) {
                    muteWithoutTimeSchedule(event, args);
                }
                //Команда со счетчиком времени.
                if (args.length == 3 && args[1].startsWith("<@")) {
                    muteWithTimeSchedule(event, args);
                }
            }
        }
    }

    private void muteWithTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        String id = args[1].startsWith("<@!") ?
                args[1].replace("<@!", "").replace(">", "") :
                args[1].replace("<@", "").replace(">", "");
        member = event.getGuild().getMemberById(id);
        if (member != null) {
            Role role = event.getGuild().getRoleById("730486590870126623");
            if (!member.getRoles().contains(role)) {
                try {
                    if (member.hasPermission(Permission.MANAGE_ROLES)) hierarchyExceptionEmbed(event);
                    int time = Integer.parseInt(args[2]);
                    event.getGuild().addRoleToMember(id, role).queue();
                    //Таймер
                    startTimer(event, time, role);
                    roleAddEmbed(event, time);
                } catch (NumberFormatException e) {
                    incorrectTimeExceptionEmbed(event);
                } catch (IllegalArgumentException e) {
                    tooLongTimeExceptionEmbed(event);
                }
            } else {
                alreadyMutedExceptionEmbed(event);
            }
        } else noMemberExceptionEmbed(event);
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
        }, time * 60000);
    }

    private void muteWithoutTimeSchedule(GuildMessageReceivedEvent event, String[] args) {
        String id = args[1].startsWith("<@!") ?
                args[1].replace("<@!", "").replace(">", "") :
                args[1].replace("<@", "").replace(">", "");
        member = event.getGuild().getMemberById(id);
        if (member != null) {
            Role role = event.getGuild().getRoleById("730486590870126623");
            if (!member.getRoles().contains(role)) {
                try {
                    if (member.hasPermission(Permission.MANAGE_ROLES)) throw new HierarchyException("");
                    event.getGuild().addRoleToMember(id, role).queue();
                    roleAddEmbed(event);
                } catch (HierarchyException e) {
                    hierarchyExceptionEmbed(event);
                }
            } else alreadyMutedExceptionEmbed(event);
        } else noMemberExceptionEmbed(event);
    }

    private void tooLongTimeExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder incorrectTime = new EmbedBuilder();
        incorrectTime.setColor(0xf2480a);
        incorrectTime.setDescription("Указано слишком большое время отстранения.\n" +
                "Оно не будет снято автоматически.");
        event.getChannel().sendMessage(incorrectTime.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        incorrectTime.clear();
        member = null;
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
            timeText = "Часов: " + time/60 + "\n" +
                    "Минут: " + time%60;
        }
        if (time >= 1440) {
            timeText = "Дней: " + time/1440 + "\n" +
            "Часов: " + (time%1440)/60 + "\n" +
            "Минут: " + (time%1440)%60;
        }
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setTitle("Отстранение:");
        succeed.setDescription(member.getUser().getName());
        succeed.addField("Время:", timeText, false);
        event.getChannel().sendMessage(succeed.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        succeed.clear();
        member = null;
    }

    private void roleRemovedEmbed(GuildMessageReceivedEvent event, Member member) {
        event.getMessage().delete().queue();
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

    private void incorrectTimeExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder incorrectTime = new EmbedBuilder();
        incorrectTime.setColor(0xf2480a);
        incorrectTime.setDescription("Указано некорректное время отстранения\n" +
                "Используй: " + App.prefix + "mute [user] [time (min)]");
        event.getChannel().sendMessage(incorrectTime.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        incorrectTime.clear();
        member = null;
    }

    private void hierarchyExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder hierarchy = new EmbedBuilder();
        hierarchy.setColor(0xf2480a);
        hierarchy.setDescription("Невозможно отстранить " + member.getUser().getName());
        event.getChannel().sendMessage(hierarchy.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        hierarchy.clear();
        member = null;
    }

    private void noMemberExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder noMember = new EmbedBuilder();
        noMember.setColor(0xf2480a);
        noMember.setDescription("Не могу найти этого пользователя на сервере :(");
        event.getChannel().sendMessage(noMember.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        noMember.clear();
    }

    private void alreadyMutedExceptionEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder muted = new EmbedBuilder();
        muted.setColor(0xf2480a);
        muted.setDescription(member.getUser().getName() + " уже отстранен.");
        event.getChannel().sendMessage(muted.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        muted.clear();
        member = null;
    }
}
