package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.configvalue.ConfigValue;
import me.domirusz24.plugincore.core.PluginInstance;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.PAPIManager;

import java.util.HashMap;
import java.util.function.Supplier;

public class DynamicString implements PluginInstance {

    private static HashMap<String, DynamicString> STRING_BY_PATH = new HashMap<>();

    public static DynamicString of(PluginCore plugin, String defaultValue, String path) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        DynamicString d = new DynamicString(plugin, defaultValue, path);
        STRING_BY_PATH.put(path, d);
        return d;
    }

    private final String path;

    private ConfigValue<String> string;

    private final PluginCore plugin;

    protected DynamicString(PluginCore plugin, String value, String path) {
        this.plugin = plugin;
        this.path = path;
        this.string = new ConfigValue<>(path, value, getCorePlugin().configM.getLanguageConfig());
    }


    public String get(PlaceholderObject... objects) {
        String s = string.getValue();
        for (PlaceholderObject object : objects) {
            s = PAPIManager.setPlaceholders(plugin, object, s);
        }
        return s;
    }

    public String get() {
        return string.getValue();
    }

    public Supplier<String> getGetter() {
        return () -> string.getValue();
    }

    public String getPath() {
        return path;
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
