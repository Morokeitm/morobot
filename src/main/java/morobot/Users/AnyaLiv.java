package morobot.Users;

import morobot.TripleMention;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AnyaLiv extends TripleMention {
    private static String[] args = {"аня", "анна", "anyaliv", "анялив", "ане", "аню", "аней", "ани", "анилив", "анюлив", "анейлив"};
    private static final String NAME = "AnyaLiv";
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
