package me.domirusz24.plugincore.config.language.dynamics;

import java.util.HashMap;

public class HoverableMessage extends DynamicString {

    private static HashMap<String, HoverableMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString hover;

    protected HoverableMessage(String defaultValue, String path, String hover) {
        super(defaultValue, path + ".message");
        this.hover = DynamicString.of(hover, path + ".hover");
    }

    public DynamicString getHover() {
        return hover;
    }

    public static HoverableMessage of(String defaultValue, String path, String hover) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        HoverableMessage d = new HoverableMessage(defaultValue, path, hover);
        STRING_BY_PATH.put(path, d);
        return d;
    }
}
