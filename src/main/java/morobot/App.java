package morobot;

import morobot.commands.Commands;
import morobot.commands.XReaction;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class App {
    static JDA builder;
    public static final String PREFIX = Config.get("prefix");

    public static void main(String[] args) throws Exception {

        builder = new JDABuilder(AccountType.BOT).setToken(Config.get("token"))
                .setActivity(Activity.watching("Руководство по написанию ботов"))
                .build();

        builder.getPresence().setStatus(OnlineStatus.ONLINE);
        builder.addEventListener(new Commands());
        builder.addEventListener(new XReaction());
    }
}
