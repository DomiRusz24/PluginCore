package me.domirusz24.plugincore.core.displayable;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.PluginInstance;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.PAPIManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringLine implements PluginInstance {

    private String message;

    private List<PlaceholderObject> objects = new ArrayList<>();

    private final PluginCore plugin;

    public StringLine(PluginCore plugin, String message, PlaceholderObject... objects) {
        this.plugin = plugin;
        this.message = message;
        this.objects.addAll(Arrays.asList(objects));
    }

    public List<PlaceholderObject> getObjects() {
        return objects;
    }

    public void setMessage(String string) {
        message = string;
    }

    public String getMessage() {
        String s = message;
        for (PlaceholderObject object : objects) {
            s = PAPIManager.setPlaceholders(plugin, object, s);
        }
        return s;
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
