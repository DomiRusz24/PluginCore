package me.domirusz24.plugincore.config.configvalue;

import me.domirusz24.plugincore.config.AbstractConfig;
import org.bukkit.Location;

public class ConfigLocation extends AbstractConfigValue<Location> {

    public ConfigLocation(String path, AbstractConfig config) {
        super(path, null, config, false);
    }

    @Override
    public void _setValue(Location value) {
        getConfig().setLocation(getPath(), value);
    }

    @Override
    public void _setDefaultValue(Location value) {}

    @Override
    protected Location getConfigValue() {
        return getConfig().getLocation(getPath());
    }
}
