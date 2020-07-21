package morobot.command.reactions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import morobot.command.Constants;
import morobot.command.IReaction;
import morobot.command.ReactionContext;
import morobot.music.PlayerManager;
import net.dv8tion.jda.api.Permission;


public class PauseReaction implements IReaction {

    @Override
    public void reactionHandle(ReactionContext event) {

        if (!event.getChannel().getId().equals(Constants.MUSIC_TEXT_CHANNEL_ID)) {
            return;
        }
        if (!hasPermission(event)) {
            event.getReaction().removeReaction(event.getAuthor()).queue();
            return;
        }
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildMusicManager(event.getGuild()).player;
        if (!player.isPaused()) {
            player.setPaused(true);
        }
    }

    @Override
    public String reactionName() {
        return "⏸";
    }

    @Override
    public boolean hasPermission(ReactionContext event) {
        return (event.getMember().hasPermission(Permission.ADMINISTRATOR) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE)));
    }
}
