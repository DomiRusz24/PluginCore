package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.*;

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
        loadConfigs();
    }

    // Plugin load
    private void loadConfigs() {
        // Config
        config = new PluginConfig("config.yml", plugin, this);

        // Language config
        languageConfig = new LanguageConfig("language.yml", plugin, this);

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

    public interface Reloadable {
        default void registerReloadable() {
            PluginCore.configM.registerReloadable(this);
        }

        default void unregisterReloadable() {
            PluginCore.configM.unregisterReloadable(this);
        }

        void onReload();
    }
}


