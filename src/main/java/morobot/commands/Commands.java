package morobot.commands;

import morobot.App;
import morobot.commands.moderation.ClearMessages;
import morobot.commands.moderation.TextMute;
import morobot.commands.music.Join;
import morobot.commands.music.Play;
import morobot.commands.music.Stop;
import morobot.commands.user.Information;
import morobot.commands.user.ShowAvatar;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class Commands extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        User author = event.getAuthor();

        if (!author.isBot()) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");

            searchCommand(event, args);
        }
    }

    private void searchCommand(GuildMessageReceivedEvent event, String[] args) {

        String command = args[0].toLowerCase();

        if (command.contains(App.PREFIX + "clear")) {
            new ClearMessages().onClearCommand(event, args);
        } else if (command.contains(App.PREFIX + "info")) {
            new Information().onInfoCommand(event, args);
        } else if (command.contains(App.PREFIX + "avatar")) {
            new ShowAvatar().onAvatarCommand(event, args);
        } else if (command.contains(App.PREFIX + "mute")) {
            new TextMute().onMuteCommand(event, args);
        } else if (command.contains(App.PREFIX + "play")) { //music command
            new Play().onPlayCommand(event, args);
        } else if (command.contains(App.PREFIX + "join")) { //music command
            new Join().onJoinCommand(event, args);
        } else if (command.contains(App.PREFIX + "stop")) { //music command
            new Stop().onStopCommand(event, args);
        }
    }
}
