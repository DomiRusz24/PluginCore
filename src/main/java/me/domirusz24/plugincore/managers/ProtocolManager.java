package me.domirusz24.plugincore.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayServerPosition;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ProtocolManager extends Manager {

    private HashMap<UUID, Set<PacketType>> EVENT_CANCEL = new HashMap<>();

    private final com.comphenix.protocol.ProtocolManager protocolLib;


    public ProtocolManager(PluginCore plugin, com.comphenix.protocol.ProtocolManager protocolLib) {
        super(plugin);
        this.protocolLib = protocolLib;

        protocolLib.addPacketListener(new PacketListener() {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                EVENT_CANCEL.putIfAbsent(packetEvent.getPlayer().getUniqueId(), new HashSet<>());
                if (EVENT_CANCEL.get(packetEvent.getPlayer().getUniqueId()).contains(packetEvent.getPacketType())) {
                    packetEvent.setCancelled(true);
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                EVENT_CANCEL.putIfAbsent(packetEvent.getPlayer().getUniqueId(), new HashSet<>());
                if (EVENT_CANCEL.get(packetEvent.getPlayer().getUniqueId()).contains(packetEvent.getPacketType())) {
                    if (packetEvent.getPacketType().equals(PacketType.Play.Server.POSITION) && hiddenPlayers.containsKey(packetEvent.getPlayer().getUniqueId())) {
                        WrapperPlayServerPosition packet = new WrapperPlayServerPosition(packetEvent.getPacket());
                        Location location = hiddenPlayers.get(packetEvent.getPlayer().getUniqueId());
                        packet.setX(location.getX());
                        packet.setY(location.getY());
                        packet.setZ(location.getZ());
                    } else {
                        packetEvent.setCancelled(true);
                    }
                }
            }

            @Override
            public ListeningWhitelist getSendingWhitelist() {
                return ListeningWhitelist.newBuilder().highest().build();
            }

            @Override
            public ListeningWhitelist getReceivingWhitelist() {
                return ListeningWhitelist.newBuilder().highest().build();
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }
        });
    }

    public boolean addEventCancel(UUID uuid, PacketType type) {
        EVENT_CANCEL.putIfAbsent(uuid, new HashSet<>());
        if (EVENT_CANCEL.get(uuid).contains(type)) return false;
        EVENT_CANCEL.get(uuid).add(type);
        return true;
    }

    public boolean removeEventCancel(UUID uuid, PacketType type) {
        EVENT_CANCEL.putIfAbsent(uuid, new HashSet<>());
        if (!EVENT_CANCEL.get(uuid).contains(type)) return false;
        EVENT_CANCEL.get(uuid).remove(type);
        return true;
    }

    public void packetTeleport(Player player, Location location) {
        WrapperPlayServerPosition packet = new WrapperPlayServerPosition();
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setPitch(location.getPitch());
        packet.setYaw(location.getYaw());
        try {
            protocolLib.sendServerPacket(player, packet.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
            PluginCore.plugin.log(Level.WARNING, "Failed packet teleporting player " + player.getName());
        }
    }

    public void removeEventCancel(UUID uuid) {
        EVENT_CANCEL.remove(uuid);
    }

    public void setToSpectator(Player player) {
        /*
        player.setGameMode(GameMode.SPECTATOR);
        try {
            protocolLib.sendServerPacket(player, ADVENTURE_PACKET);
        } catch (InvocationTargetException e) {
            ProBending.plugin.log(Level.WARNING, "An error has occurred while setting players GameMode to spectator!");
        }
        player.setAllowFlight(true);
        player.setFlying(true);
         */
        setGameMode(player, GameMode.SPECTATOR);
        Location newLoc = player.getLocation().clone();
        newLoc.setY(newLoc.getY() + 50);
        packetTeleport(player, newLoc);
        addEventCancel(player.getUniqueId(), PacketType.Play.Server.POSITION);
        hiddenPlayers.put(player.getUniqueId(), newLoc);
        toggleVisibility(player, true);
    }

    public void setGameMode(Player player, GameMode mode) {
        if (hiddenPlayers.containsKey(player.getUniqueId())) {
            removeEventCancel(player.getUniqueId(), PacketType.Play.Server.POSITION);
            toggleVisibility(player, false);
            hiddenPlayers.remove(player.getUniqueId());
        }
        player.setGameMode(mode);
    }

    private HashMap<UUID, Location> hiddenPlayers = new HashMap<>();

    public void onPlayerLeave(Player player) {
        for (UUID hidden : hiddenPlayers.keySet()) {
            player.showPlayer(Bukkit.getPlayer(hidden));
        }
        removeEventCancel(player.getUniqueId());
    }

    public void onPlayerEnter(Player player) {
        for (UUID hidden : hiddenPlayers.keySet()) {
            player.hidePlayer(Bukkit.getPlayer(hidden));
        }
        EVENT_CANCEL.putIfAbsent(player.getUniqueId(), new HashSet<>());
    }

    private void toggleVisibility(Player player, boolean hide) {
        if (hide) {
            for (Player receiver : Bukkit.getOnlinePlayers()) {
                receiver.hidePlayer(player);
            }
        } else {
            for (Player receiver : Bukkit.getOnlinePlayers()) {
                receiver.showPlayer(player);
            }
        }
    }
}
