package morobot.command;

import java.util.List;

public interface ICommand {
    void commandHandle(CommandContext event);
    String commandName();
    default List<String> getAliases() {
        return List.of();
    }
}
