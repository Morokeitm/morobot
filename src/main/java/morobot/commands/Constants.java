package morobot.commands;

import morobot.App;

public class Constants {
    //Константы класса "TextMute"
    public static final String MUTE_ROLE = "730486590870126623";
    public static final String WRONG_COMMAND = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]";
    public static final String NO_PERMISSIONS = "У тебя нет прав на добавление ролей.";
    public static final String CANT_FIND_USER = "Не могу найти этого пользователя на сервере :(";
    public static final String TOO_BIG_MUTE_TIME = "Указано слишком большое время отстранения.\nУкажи в диапазоне [1 - 34560] минут.";
    public static final String WRONG_MUTE_TIME = "Указано некорректное время отстранения\nИспользуй: " + App.prefix + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 34560] минут.";

    //Константы класса "ShowAvatar"
    public static final String NO_AVATAR = "У пользователя отсутствует картинка на аватаре.";
    public static final String CANT_FIND_MEMBER = "Не могу найти этого пользователя на сервере :(";
    public static final String NO_SELF_AVATAR = "У тебя нет картинки на аватаре, что я тебе показать должен?";
    public static final String COMMAND_USAGE = "Используй: " + App.prefix + "avatar @User";
    public static final String NEED_TO_MENTION= "Нужно упомянуть пользователя";

    //Константы класса "ClearMessages"
    public static final String USAGE = "Используй: " + App.prefix + "clear [1-99]";
    public static final String NOT_A_DIGIT = "Указанное значение не является числом.";
    public static final String NO_COUNT_OF_MESSAGES = "Не указано количество удаляемых сообщений.";
    public static final String WRONG_COUNT_OF_MESSAGES = "Указано недопустимое количество сообщений.";
    public static final String WHO_ABLE_TO_USE_COMMAND = "Команда доступна только пользователям с правами удаления сообщений";
    public static final String CORRECT_COUNT_DESCRIPTION = "Возможное количество: 1-99.";
    public static final String TRY_TO_DELETE_OLD_MESSAGES = "Попытка удалить старые сообщения.";
    public static final String NOT_ABLE_TO_DELETE_OLD_MESSAGES = "Нельзя удалить сообщения, оставленные более двух недель назад.";
    public static final String NO_PERMISSION_TO_DELETE_MESSAGES = "Нет прав на удаление сообщений.";
}
