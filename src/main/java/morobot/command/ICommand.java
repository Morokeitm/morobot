package morobot.command;

import java.util.List;

public interface ICommand {
    void commandHandle(CommandContext event);
    String commandName();
    String getHelp();
    boolean hasPermission(CommandContext event);
    default List<String> getAliases() {
        return List.of();
    }
}
