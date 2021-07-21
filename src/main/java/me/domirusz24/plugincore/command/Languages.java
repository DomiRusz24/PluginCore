package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.config.annotations.Language;

public final class Languages {

    @Language("Command.Success")
    public static String SUCCESS = "&aSuccess!";
    @Language("Command.Fail")
    public static String FAIL = "&cCommand couldn't be executed!";
    @Language("Command.FailContactAdmins")
    public static String FAIL_ERROR = "&c&lAn internal error has occurred! Please contact server admins.";

    @Language("Command.InsufficientPermissions")
    public static String INSUFFICIENT_PERMS = "You don't have the permission to use this command!";

    @Language("Command.MustBePlayer")
    public static String MUST_BE_PLAYER = "You must be a player to execute this command!";

    @Language("Command.Usage")
    public static String USAGE = "Usage: ";

    @Language("Command.Description")
    public static String COMMAND_DESCRIPTION = "Description: ";

    @Language("Command.Yes")
    public static String YES = "&aYes";

    @Language("Command.No")
    public static String NO = "&4No";

    @Language("Command.PlayerNotOnline")
    public static String PLAYER_NOT_ONLINE = "Player %player% is not online!";

    @Language("Command.LocationIsNotSet")
    public static String LOCATION_NOT_SET = "&cLocation is not set!";

    @Language("Command.IncorrectSelection")
    public static String INCORRECT_SELECTION = "&cIncorrect selection!";


}
