package me.domirusz24.plugincore.config.language.dynamics;

import java.util.HashMap;

public class SuggestMessage extends HoverableMessage {
    private static HashMap<String, SuggestMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString suggest;

    protected SuggestMessage(String defaultValue, String path, String hover, String suggest) {
        super(defaultValue, path, hover);
        this.suggest = DynamicString.of(suggest, path + ".suggest");
    }

    public DynamicString getCommand() {
        return suggest;
    }

    public static SuggestMessage of(String defaultValue, String path, String hover, String suggest) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        SuggestMessage d = new SuggestMessage(defaultValue, path, hover, suggest);
        STRING_BY_PATH.put(path, d);
        return d;
    }
}
