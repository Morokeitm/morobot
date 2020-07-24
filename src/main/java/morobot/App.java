package morobot;

import morobot.database.SQLiteDataSource;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class App {

    public static final String PREFIX = Config.get("prefix");

    private App() throws Exception {
        SQLiteDataSource.getConnection();

       new JDABuilder()
                .setToken(Config.get("token"))
                .addEventListeners(new Listener())
                .setActivity(Activity.watching("!help"))
                .build();

    }

    public static void main(String[] args) throws Exception {
        new App();
    }
}
