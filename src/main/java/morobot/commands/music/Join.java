package morobot.commands.music;

import morobot.commands.Constants;
import morobot.commands.CommandsStuff;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class Join extends CommandsStuff {

    public void onJoinCommand(GuildMessageReceivedEvent event, String[] args) {

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
            infoEmbed(event, Constants.ALREADY_JOINED);
        } else {
            event.getGuild().getAudioManager()
                    .openAudioConnection(event.getGuild().getVoiceChannelById(Constants.MUSIC_CHANNEL_ID));
            joinedToMusicChannelEmbed(event);
        }
    }

    private void joinedToMusicChannelEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder joined = new EmbedBuilder();
        joined.setColor(0x14f51b);
        joined.setDescription(Constants.JOINED);
        event.getChannel().sendMessage(joined.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        joined.clear();
    }
}
