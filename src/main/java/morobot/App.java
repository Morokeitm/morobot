package morobot;

import morobot.Commands.ClearMessages;
import morobot.Commands.Information;
import morobot.Commands.MentionHandler;
import morobot.Commands.ShowAvatar;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class App {
    static JDA builder;
    public static String prefix = "!";

    public static void main(String[] args) throws Exception {
        final String token = args[0];
        builder = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("Руководство по написанию ботов"))
                .build();

        builder.getPresence().setStatus(OnlineStatus.ONLINE);
        builder.addEventListener(new ClearMessages()); //Команда удаления сообщений.
        builder.addEventListener(new Information()); //Команда показа информации о боте.
        builder.addEventListener(new MentionHandler()); //Поверка упоминания отдельных пользователей.
        builder.addEventListener(new ShowAvatar()); //Команда отображения аватара.
    }
}
