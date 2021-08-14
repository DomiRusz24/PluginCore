package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.configvalue.ConfigValue;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.ConfigManager;
import me.domirusz24.plugincore.managers.PAPIManager;
import me.domirusz24.plugincore.managers.database.values.StringValue;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Bukkit;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.function.Supplier;

public class DynamicString {

    private static HashMap<String, DynamicString> STRING_BY_PATH = new HashMap<>();

    public static DynamicString of(String defaultValue, String path) {
        if (STRING_BY_PATH.containsKey(path)) {
            return STRING_BY_PATH.get(path);
        }
        DynamicString d = new DynamicString(defaultValue, path);
        STRING_BY_PATH.put(path, d);
        return d;
    }

    private final String path;

    private ConfigValue<String> string;

    protected DynamicString(String value, String path) {
        this.path = path;
        this.string = new ConfigValue<>(path, value, PluginCore.configM.getLanguageConfig());
        System.out.println(value + " -> " + path);
    }


    public String get(PlaceholderObject... objects) {
        String s = string.getValue();
        for (PlaceholderObject object : objects) {
            s = PAPIManager.setPlaceholders(object, s);
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
}
