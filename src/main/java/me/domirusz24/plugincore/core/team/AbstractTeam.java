package me.domirusz24.plugincore.core.team;

import me.domirusz24.plugincore.core.players.AbstractPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractTeam<T extends AbstractPlayer> {

    private final String name;
    protected final ArrayList<T> players;
    private final int size;


    public AbstractTeam(String name, int size) {
        this.name = name;
        this.size = size;
        players = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            players.add(null);
        }
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

    public void sendMessage(String message) {
        getPlayers().forEach((p) -> p.getPlayer().sendMessage(message));
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        getPlayers().forEach((p) -> p.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut));
    }

}
