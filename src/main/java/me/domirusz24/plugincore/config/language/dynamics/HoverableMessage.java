package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.HashMap;

public class HoverableMessage extends DynamicString {

    private static HashMap<String, HoverableMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString hover;

    protected HoverableMessage(PluginCore plugin, String defaultValue, String path, String hover) {
        super(plugin, defaultValue, path + ".message");
        this.hover = DynamicString.of(plugin, hover, path + ".hover");
    }

    public TextComponent getText(PlaceholderObject... objects) {
        TextComponent t = new TextComponent(get(objects));
        t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getHover().get(objects))));
        return t;
    }

    public DynamicString getHover() {
        return hover;
    }

    public static HoverableMessage of(PluginCore plugin, String defaultValue, String path, String hover) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        HoverableMessage d = new HoverableMessage(plugin, defaultValue, path, hover);
        STRING_BY_PATH.put(path, d);
        return d;
    }
}
