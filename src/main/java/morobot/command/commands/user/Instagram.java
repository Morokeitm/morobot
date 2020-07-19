package morobot.command.commands.user;

import com.fasterxml.jackson.databind.JsonNode;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import morobot.App;
import morobot.command.CommandContext;
import morobot.command.CommandsStuff;
import morobot.command.Constants;
import morobot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class Instagram extends CommandsStuff implements ICommand {

    @Override
    public void commandHandle(CommandContext event) {
        final List<String> args = event.getArgs();
        final TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            errorEmbed(event, Constants.NO_USER_NAME);
            return;
        }
        final String usn = args.get(0);
        WebUtils.ins.getJSONObject("https://apis.duncte123.me/insta/" + usn).async((json) -> {
            if (!json.get("success").asBoolean()) {
                errorEmbed(event, json.get("error").get("message").asText());
                return;
            }

            final JsonNode user = json.get("user");
            final String username = user.get("username").asText();
            final String pfp = user.get("profile_pic_url").asText();
            final String biography = user.get("biography").asText();
            final boolean isPrivate = user.get("is_private").asBoolean();
            final int following = user.get("following").get("count").asInt();
            final int followers = user.get("followers").get("count").asInt();
            final int uploads = user.get("uploads").get("count").asInt();

            final EmbedBuilder instagram = EmbedUtils.defaultEmbed()
                    .setTitle("Instagram info of " + username, "https://www.instagram.com/" + username)
                    .setThumbnail(pfp)
                    .setDescription(String.format(
                            "**Приватный аккаунт:** "+ (isPrivate ? "да" : "нет") +"\n**Биография:** %s\n**Подписан(а):** %s\n**Подписчики:** %s\n**Публикаций:** %s",
                            biography,
                            following,
                            followers,
                            uploads
                    ))
                    .setImage(getLatestImage(json.get("images")));

            channel.sendMessage(instagram.build()).queue();
        });
    }

    private String getLatestImage(JsonNode json) {
        if (!json.isArray()) {
            return null;
        }
        if (json.size() == 0) {
            return null;
        }
        return json.get(0).get("url").asText();
    }

    @Override
    public String commandName() {
        return "instagram";
    }

    @Override
    public String getHelp() {
        return "Вытягивает общую информацию пользователя и последнюю публикацию из инстаграма.\n" +
                "Использование: \"" + App.PREFIX + this.commandName() + " [имя в instagram]\"";
    }

    @Override
    public boolean hasPermission(CommandContext event) {
        return true;
    }
}
