package morobot.commands;

import morobot.App;

public class Constants {
    //Константы класса "TextMute"
    static final String MUTE_ROLE = "730486590870126623";
    static final String WRONG_COMMAND = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]";
    static final String NO_PERMISSIONS = "У тебя нет прав на добавление ролей.";
    static final String CANT_FIND_USER = "Не могу найти этого пользователя на сервере :(";
    static final String TOO_BIG_MUTE_TIME = "Указано слишком большое время отстранения.\nУкажи в диапазоне [1 - 34560] минут.";
    static final String WRONG_MUTE_TIME = "Указано некорректное время отстранения\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 34560] минут.";

    //Константы класса "ShowAvatar"
    static final String NO_AVATAR = "У пользователя отсутствует картинка на аватаре.";
    static final String CANT_FIND_MEMBER = "Не могу найти этого пользователя на сервере :(";
    static final String TOO_MANY_MEMBERS = "Такое имя у нескольких человек в дискорде.\nПопробуй указать имя пользователя на сервере, или упомянуть его через @User."; // + "TextMute"
    static final String TOO_MANY_USERS = "Такое имя у нескольких человек на сервере.\nПопробуй указать имя пользователя в дискорде, или упомянуть его через @User."; // + "TextMute"
    static final String NO_SELF_AVATAR = "У тебя нет картинки на аватаре, что я тебе показать должен?";

    //Константы класса "ClearMessages"
    static final String USAGE = "Используй: " + App.PREFIX + "clear [1-99]";
    static final String NOT_A_DIGIT = "Указанное значение не является числом.";
    static final String NO_COUNT_OF_MESSAGES = "Не указано количество удаляемых сообщений.";
    static final String WRONG_COUNT_OF_MESSAGES = "Указано недопустимое количество сообщений.";
    static final String WHO_ABLE_TO_USE_COMMAND = "Команда доступна только пользователям с правами удаления сообщений";
    static final String CORRECT_COUNT_DESCRIPTION = "Возможное количество: 1-99.";
    static final String TRY_TO_DELETE_OLD_MESSAGES = "Попытка удалить старые сообщения.";
    static final String NOT_ABLE_TO_DELETE_OLD_MESSAGES = "Нельзя удалить сообщения, оставленные более двух недель назад.";
    static final String NO_PERMISSION_TO_DELETE_MESSAGES = "Нет прав на удаление сообщений.";
}
