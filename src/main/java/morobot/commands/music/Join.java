package morobot.commands.music;

import morobot.commands.Constants;
import morobot.commands.XReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.TimeUnit;

public class Join {

    public void onJoinCommand(GuildMessageReceivedEvent event, String[] args) {

        String channelId = event.getChannel().getId();

        if (channelId.equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            if (args.length == 1) {
                isConnected(event);
            }
        } else {
            errorEmbed(event);
        }
    }

    private void isConnected(GuildMessageReceivedEvent event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            alreadyConnectedEmbed(event);
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

    private void alreadyConnectedEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder succeed = new EmbedBuilder();
        succeed.setColor(0xfcba03);
        succeed.setDescription(Constants.ALREADY_JOINED);
        event.getChannel().sendMessage(succeed.build())
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        succeed.clear();
    }

    private static void errorEmbed(GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(0xf2480a);
        error.setDescription(Constants.WRONG_CHANNEL);
        RestAction<Message> action = event.getChannel().sendMessage(error.build());
        action.queue((message) -> {
            //Добавляем реакцию ❌ к сообщению об ошибке
            message.addReaction("❌").queue();
            XReaction.putAndSave(message.getId(), event.getMember().getId());
            message.delete().queueAfter(15, TimeUnit.SECONDS);
        });
        error.clear();
    }
}
