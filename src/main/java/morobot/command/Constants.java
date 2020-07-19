package morobot.command;

import morobot.App;

public class Constants {
    //Общие
    public static final String BOT_ID = "730438468253122630";
    public static final String MUSIC_CHANNEL_ID = "731809802027532309";
    public static final String MUSIC_TEXT_CHANNEL_ID = "731830804023607367";
    public static final String DJ_ROLE = "731867396066902076";
    public static final String MUTE_ROLE = "730486590870126623";
    public static final String WRONG_CHANNEL = "эту команду можно использовать только в канале \"music\"";
    public static final String CANT_FIND_USER = "такого пользователя нет на сервере.";
    public static final String NO_PERMISSION_TO_USE_COMMAND = "у тебя недостаточно прав для использования этой команды.";
    public static final String NO_CONNECTION = "я не подключен к каналу \"music\". Для подключения используй команду !join.";

    //Константы класса "TextMute"
    public static final String WRONG_COMMAND = "ты некорректно написал(а) команду \"mute\".\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]";
    public static final String NO_PERMISSIONS_TO_ADDING_ROLES = "у тебя нет прав на добавление ролей.";
    public static final String TOO_BIG_MUTE_TIME = "ты указал(а) слишком большое время отстранения.\nУкажи в диапазоне [1 - 34560] минут.";
    public static final String WRONG_MUTE_TIME = "ты указал(а) некорректное время отстранения\nИспользуй: " + App.PREFIX + "mute [user] [time (optional, min)]\nВремя отстранения, при этом, может быть [1 - 34560] минут.";

    //Константы класса "ShowAvatar"
    public static final String NO_AVATAR = "у этого человека нет изображения.";
    public static final String TOO_MANY_MEMBERS = "такое имя у нескольких человек в дискорде.\nПопробуй указать имя пользователя на сервере, или упомянуть его через @User."; // + "TextMute"
    public static final String TOO_MANY_USERS = "такое имя у нескольких человек на сервере.\nПопробуй указать имя пользователя в дискорде, или упомянуть его через @User."; // + "TextMute"
    public static final String NO_SELF_AVATAR = "у тебя нет картинки на аватаре.";

    //Константы класса "UserInfo"
    public static final String NO_USER_ARG = "Не указан пользователь.";

    //Константы класса "ClearMessages"
    public static final String USAGE = "Используй: " + App.PREFIX + "clear [1-99]";
    public static final String NOT_A_DIGIT = "ты указал(а) неправильное число, или вовсе не число.";
    public static final String NO_COUNT_OF_MESSAGES = "ты не указал(а) количество удаляемых сообщений.";
    public static final String WRONG_COUNT_OF_MESSAGES = "ты указал(а) недопустимое количество сообщений.";
    public static final String WHO_ABLE_TO_USE_COMMAND = "Команда доступна только пользователям с правами удаления сообщений";
    public static final String CORRECT_COUNT_DESCRIPTION = "Возможное количество: 1-99.";
    public static final String TRY_TO_DELETE_OLD_MESSAGES = "ты пытаешься удалить старые сообщения.";
    public static final String NOT_ABLE_TO_DELETE_OLD_MESSAGES = "Нельзя удалить сообщения, оставленные более двух недель назад.";
    public static final String NO_PERMISSION_TO_DELETE_MESSAGES = "у тебя нет прав на удаление сообщений.";

    //Константы для класса "Play"
    public static final String NO_URL = "укажи ссылку к источнику.";
    public static final String NOT_AN_URL = "указанная ссылка некорректна.";
    public static final String CONTINUE_TO_PLAY = "Трек снят с паузы.";

    //Константы для класса "Join"
    public static final String JOINED = "Подключился к каналу \"music\"";
    public static final String ALREADY_JOINED = "Я уже подключен к каналу.";

    //Константы для класса "Leave"
    public static final String LEFT = "Отключился от канала \"music\"";
    public static final String ALREADY_LEFT = "Я не подключен к каналу \"music\"";

    //Константы для класса "Stop"
    public static final String STOP_MUSIC_AND_DELETE_QUEUE = "Воспроизведение остановлено, очередь очищена.";
    public static final String DELETE_QUEUE = "Очередь очищена.";

    //Константы для класса "Track"
    public static final String NO_TRACK_PLAYING = "Сейчас ничего не играет.";

    //Константы для класса "Pause"
    public static final String TRACK_PAUSED = "Трек поставлен на паузу.";
    public static final String TRACK_ALREADY_PAUSED = "Трек уже поставлен на паузу.";

    //Константы для класса "Queue"
    public static final String QUEUE_IS_EMPTY = "В очереди ничего нет.";
}
