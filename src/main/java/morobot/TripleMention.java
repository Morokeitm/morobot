package morobot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TripleMention {
    protected boolean mentionExists(String[] messageWords, List<String> namesList) {
        AtomicBoolean mentioned = new AtomicBoolean(false);
        namesList.forEach(word -> {
            for (String messageWord : messageWords) {
                if (messageWord.equalsIgnoreCase(word)) {
                    mentioned.set(true);
                    break;
                }
            }
        });
        return mentioned.get();
    }

    protected void countTime(long startTime, long endTime, String name, GuildMessageReceivedEvent event) {
        BigInteger start = new BigInteger(String.valueOf(startTime));
        BigInteger difference = new BigInteger(String.valueOf(endTime));
        difference = difference.subtract(start);
        if (difference.longValue() < 20000L) {
            pingOnTripleMention(event, name);
        }
    }

    protected static void pingOnTripleMention(GuildMessageReceivedEvent event, String name) {
        event.getChannel().sendTyping().queue();
        event.getChannel()
                .sendMessage("Тебя тут как-то слишком часто упоминают, " +
                        App.builder.getUsersByName(name, false).get(0).getAsMention() +
                        ". Может, стоит обратить внимание?")
                .queue();
    }
}
