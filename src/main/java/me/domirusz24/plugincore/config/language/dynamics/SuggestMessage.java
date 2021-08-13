package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;

public class SuggestMessage extends HoverableMessage {
    private static HashMap<String, SuggestMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString suggest;

    protected SuggestMessage(String defaultValue, String path, String hover, String suggest) {
        super(defaultValue, path, hover);
        this.suggest = DynamicString.of(suggest, path + ".suggest");
    }

    @Override
    public TextComponent getText(PlaceholderObject... objects) {
        TextComponent t = super.getText(objects);
        t.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getSuggest().get(objects)));
        return t;
    }

    public DynamicString getSuggest() {
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
