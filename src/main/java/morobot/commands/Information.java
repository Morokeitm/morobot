package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;

public class Information extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (!event.getAuthor().isBot() && event.getMember() != null) {
            if (args[0].equalsIgnoreCase(App.prefix + "info")) {
                event.getMessage().delete().queue();
                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("~ Информация о боте ~");
                info.setDescription("Для тебя доступны следующие команды:");
                info.addField(App.prefix + "AVATAR [USER]",
                        "Я покажу аватар пользователя, имя которого ты укажешь.\n" +
                                "Если никого не укажешь, то сможешь полюбоваться на свой аватар.", false);
                if (event.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
                    info.addField(App.prefix + "CLEAR [1-99]",
                            "Написали много лишнего? Я удалю эти сообщения!\n" +
                                    "Имей в виду, что за один раз я смогу удалить не больше 99 сообщений.", false);
                }
                if (event.getMember().getPermissions().contains(Permission.MANAGE_ROLES)) {
                    info.addField(App.prefix + "MUTE [USER] [TIME (optional, min)]",
                            "Кто-то плохо себя ведет? Пусть подумает над своим поведением.\n" +
                                    "Если не укажешь продолжительность, отстранение придется снимать вручную.", false);
                }
                info.setFooter("Developed by Morokei_tm", "https://cdn.discordapp.com/avatars/319137115139080192/27b8ae9889feb379950af141841d48b4.png");
                info.setColor(0x2374de);
                //Добавляем реакцию ❌ к сообщению
                RestAction<Message> action = event.getChannel().sendMessage(info.build());
                action.queue(message -> {
                    message.addReaction("❌").queue();
                    XReaction.putAndSave(message.getId(), event.getMember().getId());
                });
                info.clear();
            }
        }
    }
}
