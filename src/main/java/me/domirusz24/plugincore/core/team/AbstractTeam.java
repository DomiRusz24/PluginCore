package me.domirusz24.plugincore.core.team;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.managers.PAPIManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractTeam<T extends PlaceholderObject> implements PlaceholderObject {

    private final String name;
    protected final ArrayList<T> players;
    private final int size;
    protected final PluginCore plugin;


    public AbstractTeam(PluginCore plugin, String name, int size) {
        this.plugin = plugin;
        this.name = name;
        this.size = size;
        players = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            players.add(null);
        }
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }

    public String getInfo() {
        return PAPIManager.setPlaceholders(plugin, this, getSyntax());
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public abstract boolean onAddPlayer(T player);

    public abstract boolean onRemovePlayer(T player);

    public abstract boolean onPurgePlayers();

    private void add(T player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, player);
                break;
            }
        }
    }

    private void remove(T player) {
        players.remove(player);
        players.add(null);
    }

    public int getCurrentSize() {
        return players.stream().filter(Objects::nonNull).toArray().length;
    }

    public boolean addPlayer(T player) {
        if (player == null) return false;
        if (!players.contains(player) && !isFull()) {
            if (onAddPlayer(player)) {
                add(player);
                return true;
            }
        }
        return false;
    }

    public boolean removePlayer(T player) {
        if (player == null) return false;
        if (players.contains(player)) {
            if (onRemovePlayer(player)) {
                remove(player);
                return true;
            }
        }
        return false;
    }

    public void purgePlayers() {
        if (onPurgePlayers()) {
            for (T player : getPlayers()) {
                removePlayer(player);
            }
        }
    }

    public List<T> getPlayers() {
        return players.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<T> getNullPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isFull() {
        return getCurrentSize() == getSize();
    }

    public abstract void sendMessage(String message);

    public abstract void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    public abstract String getPlayerInfoSyntax();

    public abstract String getNullPlayerInfoSyntax();

    public abstract String getSyntax();

    protected abstract String _onPlaceholderRequest(String message);

    public String onPlaceholderRequest(String message) {
        return onPlaceholderRequest(message, false);
    }

    @Override
    public String placeHolderPrefix() {
        return "team";
    }

    public String onPlaceholderRequest(String message, boolean nullValues) {
        if (message.equalsIgnoreCase("players")) {
            StringBuilder playerInfo = new StringBuilder();
            int index = 0;
            for (T object : getNullPlayers()) {
                if (object == null) {
                    if (nullValues) {
                        index++;
                        playerInfo.append(getNullPlayerInfoSyntax().replaceAll("%row%", String.valueOf(index)));
                    }
                } else {
                    index++;
                    playerInfo.append(PAPIManager.setPlaceholders(plugin, object, getPlayerInfoSyntax().replaceAll("%row%", String.valueOf(index))));
                }
            }
            return playerInfo.toString();
        } else if (message.equalsIgnoreCase("name")) {
            return getName();
        } else if (message.equalsIgnoreCase("size")) {
            return String.valueOf(getSize());
        } else {
            return _onPlaceholderRequest(message);
        }
    }
}
