package morobot.users;

import morobot.TripleMention;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class Strelok extends TripleMention {
    private static String[] args = {"стрелок", "диана", "strelok", "диане", "диану", "дианой", "стрелка", "стрелку", "стрелке", "дианы"};
    private static final String NAME = "STRELOK";
    private static int count = 0;
    private static long startTime;
    private static long endTime;

    public void checkMention(GuildMessageReceivedEvent event) {
        List<String> namesList = Arrays.asList(args);
        String[] messageWords = event.getMessage().getContentRaw().split("\\s+");
        if (mentionExists(messageWords, namesList)) {
            if (count == 0) startTime = System.currentTimeMillis();
            count++;
            System.out.println("Упоминаний " + NAME + ": " + count);
        }
        if (count == 3) {
            count = 0;
            endTime = System.currentTimeMillis();
            countTime(startTime, endTime, NAME, event);
        }
    }
}
