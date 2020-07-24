package morobot.command.commands;

import morobot.CommandManager;
import morobot.Config;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.ICommand;
import morobot.command.reactions.DeleteReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Help extends CommandsStuff implements ICommand {

    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.manager = manager;
    }

    private void commandListEmbed (CommandContext event, String description) {
        event.getMessage().delete().queue();
        EmbedBuilder info = new EmbedBuilder();
        info.setDescription(description);
        info.setColor(0x2374de);
        //Добавляем реакцию ❌ к сообщению
        RestAction<Message> action = event.getChannel().sendMessage(info.build());
        action.queue(message -> {
            message.addReaction("❌").queue();
            DeleteReaction.putAndSave(message.getId(), event.getMember().getId());
        });
        info.clear();
    }

    private void helpEmbed (CommandContext event, ICommand command) {
        event.getMessage().delete().queue();
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Команда \"" + command.commandName() +"\"");
        if (!command.hasPermission(event)) {
            info.setColor(0xf2480a);
            info.setDescription("\n\n❌ Недоступна для тебя.");
        } else {
            info.setColor(0x2374de);
            info.setDescription(command.getHelp());
        }
        event.getChannel().sendMessage(info.build())
                .delay(15, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        info.clear();
    }

    @Override
    public void commandHandle(CommandContext event) {
        List<String> args = event.getArgs();
        final int[] i = {0};

        if (args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Список команд:\n");
            manager.getCommands().stream()
                    .map(ICommand::commandName)
                    .forEach(command -> {
                        i[0]++;
                        builder
                                .append("\"")
                                .append(Config.get("prefix"))
                                .append(command)
                                .append("\"")
                                .append(i[0] % 7 == 0 ? "\n" : "  ");});
            commandListEmbed(event, builder.toString());
            return;
        }
        String searchedCommand = args.get(0);
        ICommand command = manager.getCommand(searchedCommand);
        if (command == null) {
            String description = "Команда \"" + searchedCommand + "\" не найдена.";
            errorEmbed(event, description);
            return;
        }
        helpEmbed(event, command);
    }

    @Override
    public String commandName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Показывает список всех команд бота, либо информацию об указанной команде.\n\n" +
                "**Использование:** \"" + Config.get("prefix") + this.commandName() + "\", " +
                "либо \"" + Config.get("prefix") + this.commandName() + " [команда]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
