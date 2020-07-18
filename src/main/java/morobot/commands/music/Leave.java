package morobot.commands.music;

import morobot.commands.CommandsStuff;
import morobot.commands.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class Leave extends CommandsStuff {

    public void onLeaveCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                        event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE))) {
                    isConnected(event);
                } else {
                    errorEmbed(event, Constants.NO_PERMISSION_TO_USE_COMMAND);
                }
            }
        } else {
            errorEmbed(event, Constants.WRONG_CHANNEL);
        }
    }

    private void isConnected(GuildMessageReceivedEvent event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().closeAudioConnection();
            leaveFromMusicChannelEmbed(event);
        } else {
            infoEmbed(event, Constants.ALREADY_LEFT);
        }
    }

    private void leaveFromMusicChannelEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder left = new EmbedBuilder();
        left.setColor(0x14f51b);
        left.setDescription(Constants.LEFT);
        event.getChannel().sendMessage(left.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        left.clear();
    }
}
