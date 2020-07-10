package morobot.commands;

import morobot.users.AnyaLiv;
import morobot.users.Strelok;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MentionHandler extends ListenerAdapter {   //Обязательно наследуемся от ListenerAdapter для обработки сообщений

//    private static AnyaLiv anyaLiv = new AnyaLiv();
//    private static Strelok strelok = new Strelok();

//    @Override
//    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {  //event - ообщение, оставленное кем-то.
//        if (!event.getAuthor().isBot()) {
//            anyaLiv.checkMention(event);
//            strelok.checkMention(event);
//        }
//    }
}
