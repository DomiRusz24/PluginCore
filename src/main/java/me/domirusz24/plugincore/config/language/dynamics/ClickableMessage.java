package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.HashMap;

public class ClickableMessage extends HoverableMessage {

    private static HashMap<String, ClickableMessage> STRING_BY_PATH = new HashMap<>();

    private final DynamicString command;

    protected ClickableMessage(PluginCore plugin, String defaultValue, String path, String hover, String command) {
        super(plugin, defaultValue, path, hover);
        this.command = DynamicString.of(plugin, command, path + ".command");
    }

    @Override
    public TextComponent getText(PlaceholderObject... objects) {
        TextComponent t = super.getText(objects);
        t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand().get(objects)));
        return t;
    }

    public DynamicString getCommand() {
        return command;
    }

    public static ClickableMessage of(PluginCore plugin, String defaultValue, String path, String hover, String command) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        ClickableMessage d = new ClickableMessage(plugin, defaultValue, path, hover, command);
        STRING_BY_PATH.put(path, d);
        return d;
    }
}
