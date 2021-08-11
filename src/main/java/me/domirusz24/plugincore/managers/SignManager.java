package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.displayable.CustomSign;

import java.util.Collection;
import java.util.HashMap;

public class SignManager extends Manager {

    private final HashMap<String, CustomSign> SIGNS = new HashMap<>();

    public SignManager(PluginCore plugin) {
        super(plugin);
    }

    public void addSign(CustomSign sign) {
        SIGNS.put(sign.getID(), sign);
    }

    public CustomSign getSign(String ID) {
        return SIGNS.getOrDefault(ID, null);
    }

    public Collection<CustomSign> getSigns() {
        return SIGNS.values();
    }
}
