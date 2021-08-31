package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.gui.CustomGUI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GUIManager extends Manager {

    private HashMap<UUID, PlayerGUIData> DATA_BY_UUID = new HashMap<>();

    public GUIManager(PluginCore plugin) {
        super(plugin);
    }

    public void register(Player player) {
        DATA_BY_UUID.put(player.getUniqueId(), new PlayerGUIData(player));
    }

    public void unregister(Player player) {
        DATA_BY_UUID.remove(player.getUniqueId());
    }

    public PlayerGUIData get(Player player) {
        if (!DATA_BY_UUID.containsKey(player.getUniqueId())) {
            register(player);
        }
        return DATA_BY_UUID.get(player.getUniqueId());
    }







    public static class PlayerGUIData {

        private Player player;

        private HashMap<Integer, CustomGUI> GUI = new HashMap<>();

        private int current = -1;

        public PlayerGUIData(Player player) {
            this.player = player;
        }

        public void addNew(CustomGUI customGUI) {
            current++;
            debug("adding " + customGUI.getTitle() + " -> " + current);
            for (Integer id : new ArrayList<>(GUI.keySet())) {
                if (id >= current) {
                    debug("removing " + GUI.get(id).getTitle() + " -> " + id);
                    GUI.remove(id);
                }
            }
            GUI.put(current, customGUI);
        }

        public CustomGUI previous() {
            current--;
            if (current >= 0) {
                debug("previous -> " + getCurrent().getTitle());
                return getCurrent();
            } else {
                debug("previous FAIL");
                current++;
                return null;
            }
        }

        public CustomGUI next() {
            current++;
            if (current < GUI.size()) {
                debug("next -> " + getCurrent().getTitle());
                return getCurrent();
            } else {
                debug("next FAIL");
                current--;
                return null;
            }
        }

        public CustomGUI peekPrevious() {
            if (current - 1 >= 0) {
                return GUI.get(current - 1);
            } else {
                return null;
            }
        }

        public CustomGUI peekNext() {
            if (current + 1 < GUI.size()) {
                return GUI.get(current + 1);
            } else {
                return null;
            }
        }

        public void goForward() {
            if (peekNext() != null) {
                peekNext().addPlayer(player, false);
                next();
            }
        }

        public void goBackwards() {
            if (peekPrevious() != null) {
                peekPrevious().addPlayer(player, false);
                previous();
            }
        }

        public void closeAll() {
            debug("CLOSE ALL");
            for (Integer id : new HashSet<>(GUI.keySet())) {
                GUI.get(id).onPlayerClose(player);
            }
            GUI.clear();
            current = -1;
        }

        public CustomGUI getCurrent() {
            if (current == -1) {
                return null;
            }
            return GUI.get(current);
        }

        public Player getPlayer() {
            return player;
        }

        public void debug(String message) {
            if (CustomGUI.debug) {
                System.out.println("(" + this.getPlayer().getName() + ") " + message);
            }
        }


    }
}
