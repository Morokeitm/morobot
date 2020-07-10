package morobot;

import morobot.commands.ClearMessages;
import morobot.commands.Information;
import morobot.commands.MentionHandler;
import morobot.commands.ShowAvatar;
import morobot.commands.TextMute;
import morobot.commands.XReaction;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class App {
    static JDA builder;
    public static String prefix = "!";

    public static void main(String[] args) throws Exception {
//        final String token = args[0]; Временно не используется
        builder = new JDABuilder(AccountType.BOT).setToken(args[0])
                .setActivity(Activity.watching("Руководство по написанию ботов"))
                .build();

        builder.getPresence().setStatus(OnlineStatus.ONLINE);
        builder.addEventListener(new ClearMessages()); //Команда удаления сообщений.
        builder.addEventListener(new Information()); //Команда показа информации о боте.
        builder.addEventListener(new MentionHandler()); //Поверка упоминания отдельных пользователей.
        builder.addEventListener(new ShowAvatar()); //Команда отображения аватара.
        builder.addEventListener(new TextMute()); //Команда выдачи роли мута (Код роли у каждого сервера разный).
        builder.addEventListener(new  XReaction());
    }
}
