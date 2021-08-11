package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.chatgui.ChatGUI;
import me.domirusz24.plugincore.core.gui.GUIItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatGUIManager extends Manager {

    private final HashMap<UUID, ChatGUI> GUI_BY_UUID = new HashMap<>();

    public ChatGUIManager(PluginCore plugin) {
        super(plugin);
    }

    public ChatGUI getChatGUI(Player player) {
        return GUI_BY_UUID.get(player.getUniqueId());
    }

    public void register(ChatGUI gui) {
        GUI_BY_UUID.put(gui.getPlayer().getUniqueId(), gui);
    }

    public void unregister(ChatGUI gui) {
        GUI_BY_UUID.remove(gui.getPlayer().getUniqueId());
    }
}
