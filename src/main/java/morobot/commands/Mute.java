package morobot.commands;

import morobot.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {

    private static Member member;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (!event.getAuthor().isBot() && args[0].equalsIgnoreCase(App.prefix + "mute")) {
            if (args.length == 2 && args[1].startsWith("<@")) {
                String id = args[1].startsWith("<@!") ?
                        args[1].replace("<@!", "").replace(">", "") :
                        args[1].replace("<@", "").replace(">", "");
                member = event.getGuild().getMemberById(id);
                Role role = event.getGuild().getRoleById("730354060900827156");
                if (!event.getGuild().getMemberById(id).getRoles().contains(role)) {
                    event.getGuild().addRoleToMember(event.getGuild().getMemberById(id), role).queue();
                    roleAdded(event);
                } else {
                    alreadyMutedException(event);
                }
            }
        }
    }

    private void alreadyMutedException(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setTitle(member.getNickname() + " уже в муте.");
        event.getChannel().sendMessage(error.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        error.clear();
        member = null;
    }

    private void roleAdded(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setTitle("Muted: " + member.getNickname() + ".");
        event.getChannel().sendMessage(succeed.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        succeed.clear();
        member = null;
    }
}
