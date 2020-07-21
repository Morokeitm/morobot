package morobot.command.reactions;

import morobot.command.Constants;
import morobot.command.IReaction;
import morobot.command.ReactionContext;
import morobot.music.PlayerManager;
import morobot.music.TrackScheduler;
import net.dv8tion.jda.api.Permission;

public class NextReaction implements IReaction {
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
        TrackScheduler scheduler = manager.getGuildMusicManager(event.getGuild()).scheduler;
        scheduler.nextTrack();
    }

    @Override
    public String reactionName() {
        return "⏩";
    }

    @Override
    public boolean hasPermission(ReactionContext event) {
        return (event.getMember().hasPermission(Permission.ADMINISTRATOR) ||
                event.getMember().getRoles().contains(event.getGuild().getRoleById(Constants.DJ_ROLE)));
    }
}
