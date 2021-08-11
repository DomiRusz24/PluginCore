package me.domirusz24.plugincore.config.language.dynamics;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.ConfigManager;

import java.util.HashMap;
import java.util.function.Supplier;

public class DynamicString implements ConfigManager.Reloadable {

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

    private String string;

    protected DynamicString(String value, String path) {
        this.path = path;
        PluginCore.configM.getLanguageConfig().addDefault(path, value);
        reload();
        registerReloadable();
    }

    private void reload() {
        string = PluginCore.configM.getLanguageConfig().getString(path);
    }

    public String get() {
        return string;
    }

    public Supplier<String> getGetter() {
        return () -> string;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void onReload() {
        reload();
    }
}
