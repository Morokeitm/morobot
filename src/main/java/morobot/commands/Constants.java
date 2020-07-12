package morobot.commands;

import morobot.App;

public class Constants {
    //Общие
    public static final String BOT_ID = "730438468253122630";
    public static final String MUSIC_CHANNEL_ID = "731809802027532309";
    public static final String MUSIC_TEXT_CHANNEL_ID = "731830804023607367";
    public static final String DJ_ROLE = "731867396066902076";
    public static final String MUTE_ROLE = "730486590870126623";
    public static final String WRONG_CHANNEL = "Эту команду можно использовать только в канале \"music\"";

    //Константы класса "TextMute"
    public static final String WRONG_COMMAND = "Команда \"mute\" написана некорректно.\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]";
    public static final String NO_PERMISSIONS_TO_ADDING_ROLES = "У тебя нет прав на добавление ролей.";
    public static final String CANT_FIND_USER = "Не могу найти этого пользователя на сервере :(";
    public static final String TOO_BIG_MUTE_TIME = "Указано слишком большое время отстранения.\nУкажи в диапазоне [1 - 34560] минут.";
    public static final String WRONG_MUTE_TIME = "Указано некорректное время отстранения\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 34560] минут.";

    //Константы класса "ShowAvatar"
    public static final String NO_AVATAR = "У пользователя отсутствует картинка на аватаре.";
    public static final String CANT_FIND_MEMBER = "Не могу найти этого пользователя на сервере :(";
    public static final String TOO_MANY_MEMBERS = "Такое имя у нескольких человек в дискорде.\nПопробуй указать имя пользователя на сервере, или упомянуть его через @User."; // + "TextMute"
    public static final String TOO_MANY_USERS = "Такое имя у нескольких человек на сервере.\nПопробуй указать имя пользователя в дискорде, или упомянуть его через @User."; // + "TextMute"
    public static final String NO_SELF_AVATAR = "У тебя нет картинки на аватаре, что я тебе показать должен?";

    //Константы класса "ClearMessages"
    public static final String USAGE = "Используй: " + App.PREFIX + "clear [1-99]";
    public static final String NOT_A_DIGIT = "Указанное значение не является числом.";
    public static final String NO_COUNT_OF_MESSAGES = "Не указано количество удаляемых сообщений.";
    public static final String WRONG_COUNT_OF_MESSAGES = "Указано недопустимое количество сообщений.";
    public static final String WHO_ABLE_TO_USE_COMMAND = "Команда доступна только пользователям с правами удаления сообщений";
    public static final String CORRECT_COUNT_DESCRIPTION = "Возможное количество: 1-99.";
    public static final String TRY_TO_DELETE_OLD_MESSAGES = "Попытка удалить старые сообщения.";
    public static final String NOT_ABLE_TO_DELETE_OLD_MESSAGES = "Нельзя удалить сообщения, оставленные более двух недель назад.";
    public static final String NO_PERMISSION_TO_DELETE_MESSAGES = "Нет прав на удаление сообщений.";

    //Константы для класса "Play"
    public static final String NO_URL = "Нужно указать ссылку к источнику.";
    public static final String NOT_AN_URL = "Указана некорректная ссылка.";
    public static final  String USE_JOIN_COMMAND_FIRST = "Сначала используй команду !join, чтобы я подключился к каналу \"music\"";

    //Константы для класса "Join"
    public static final String JOINED = "Подключился к каналу \"music\"";
    public static final String ALREADY_JOINED = "Я уже подключен к каналу.";

    //Константы для класса "Stop"
    public static final String STOP_MUSIC_AND_DELETE_QUEUE = "Воспроизведение остановлено, очередь очищена.";
    public static final String DELETE_QUEUE = "Очередь очищена.";
    public static final String NO_PERMISSION_TO_USE_STOP = "Недостаточно прав для использования этой команды.";
}
