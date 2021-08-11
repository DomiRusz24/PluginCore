package me.domirusz24.plugincore.config.language.dynamics;

import java.util.HashMap;

public class ClickableMessage extends HoverableMessage {

    private static HashMap<String, ClickableMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString command;

    protected ClickableMessage(String defaultValue, String path, String hover, String command) {
        super(defaultValue, path, hover);
        this.command = DynamicString.of(command, path + ".command");
    }

    public DynamicString getCommand() {
        return command;
    }

    public static ClickableMessage of(String defaultValue, String path, String hover, String command) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        ClickableMessage d = new ClickableMessage(defaultValue, path, hover, command);
        STRING_BY_PATH.put(path, d);
        return d;
    }
}
