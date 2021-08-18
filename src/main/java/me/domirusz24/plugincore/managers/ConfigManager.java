package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.*;
import me.domirusz24.plugincore.config.language.LanguageConfig;
import me.domirusz24.plugincore.core.PluginInstance;

import java.util.ArrayList;

public class ConfigManager extends Manager {

    private PluginConfig config;

    private SchematicLocationsConfig schematicConfig;

    private PhantomSchematics phantomSchematics;


    private LanguageConfig languageConfig;

    private final ArrayList<Reloadable> reloadables = new ArrayList<>();

    private final ArrayList<AbstractConfig> configs = new ArrayList<>();

    public ConfigManager(PluginCore plugin) {
        super(plugin);
    }

    // Plugin load
    public void loadConfigs() {
        // Config
        config = new PluginConfig("config.yml", plugin, this);

        // Language config
        languageConfig = new LanguageConfig("language.yml", plugin, this);
        getLanguageConfig().registerAnnotations();

        schematicConfig = new SchematicLocationsConfig("SchematicMinLocations.yml", plugin, this);

        phantomSchematics = new PhantomSchematics("PhantomSchematics.yml", plugin, this);
    }

    // Plugin reload
    public boolean reloadConfigs() {
        boolean success = true;
        for (AbstractConfig config : configs) {
            if (!config.reload()) {
                success = false;
            }
        }
        if (success) {
            reloadables.forEach(Reloadable::onReload);
        }
        return success;
    }

    public void registerConfig(AbstractConfig config) {
        configs.add(config);
    }

    public PluginConfig getConfig() {
        return config;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public PhantomSchematics getPhantomSchematics() {
        return phantomSchematics;
    }

    public SchematicLocationsConfig getSchematicConfig() {
        return schematicConfig;
    }

    public void registerReloadable(Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    public void unregisterReloadable(Reloadable reloadable) {
        reloadables.remove(reloadable);
    }

    public interface Reloadable extends PluginInstance {
        default void registerReloadable() {
            getCorePlugin().configM.registerReloadable(this);
        }

        default void unregisterReloadable() {
            getCorePlugin().configM.unregisterReloadable(this);
        }

        void onReload();
    }
}


