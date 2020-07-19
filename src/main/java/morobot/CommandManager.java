package morobot;

import morobot.command.CommandContext;
import morobot.command.ICommand;
import morobot.command.commands.Help;
import morobot.command.commands.moderation.Ping;
import morobot.command.commands.moderation.ClearMessages;
import morobot.command.commands.moderation.TextMute;
import morobot.command.commands.music.Join;
import morobot.command.commands.music.Leave;
import morobot.command.commands.music.Pause;
import morobot.command.commands.music.Play;
import morobot.command.commands.music.Queue;
import morobot.command.commands.music.Stop;
import morobot.command.commands.music.Track;
import morobot.command.commands.user.ShowAvatar;
import morobot.command.commands.user.UserInfo;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Help(this));
        addCommand(new Ping());
        addCommand(new ShowAvatar());
        addCommand(new UserInfo());
        addCommand(new ClearMessages());
        addCommand(new TextMute());
        addCommand(new Join());
        addCommand(new Leave());
        addCommand(new Pause());
        addCommand(new Play());
        addCommand(new Queue());
        addCommand(new Stop());
        addCommand(new Track());
    }

    private void addCommand(ICommand command) {
        boolean nameFound = this.commands.stream()
                .anyMatch(cmd -> cmd.commandName()
                        .toLowerCase()
                        .equals(command.commandName()));

        if (nameFound) {
            throw new IllegalArgumentException("Команда с таким названием уже существует!");
        }
        commands.add(command);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        String searchInLowerCase = search.toLowerCase();
        for (ICommand command : this.commands) {
            if (command.commandName().equals(searchInLowerCase) || command.getAliases().contains(searchInLowerCase)) {
                return command;
            }
        }
        return null;
    }

    void handle(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")), "")
                .split("\\s+");
        String command = args[0].toLowerCase();
        ICommand cmd = this.getCommand(command);

        if (cmd != null) {
            List<String> words = Arrays.asList(args).subList(1, args.length);
            CommandContext commandContext = new CommandContext(event, words);
            cmd.commandHandle(commandContext);
        }
    }
}
