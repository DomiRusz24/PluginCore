package me.domirusz24.plugincore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.core.protocollib.ProtocolUtil;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayClientPositionLook;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayServerMapChunk;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayServerPosition;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayerClientPosition;
import me.domirusz24.plugincore.util.Pair;
import me.domirusz24.plugincore.util.PerTick;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.rmi.CORBA.Util;
import java.util.*;

public class CoreListener implements Listener {
    private static final HashSet<PerTick> PER_TICKABLE = new HashSet<>();

    public static void hookInListener(PerTick tickable) {
        PER_TICKABLE.add(tickable);
    }

    public static void removeListener(PerTick tickable) {
        PER_TICKABLE.remove(tickable);
    }

    public CoreListener() {
        Bukkit.getScheduler().runTaskTimer(PluginCore.plugin, () -> {
            for (PerTick perTick : new ArrayList<>(PER_TICKABLE)) {
                perTick.onTick();
            }
        }, 1, 1);

        PluginCore.protocol.addPacketListener(new PacketListener() {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                if (packetEvent.getPacketType().equals(PacketType.Play.Server.POSITION)) {
                    if (ProtocolUtil.TELEPORT_LOCATION.containsKey(packetEvent.getPlayer().getEntityId())) {
                        Location loc = ProtocolUtil.TELEPORT_LOCATION.get(packetEvent.getPlayer().getEntityId());
                        if (packetEvent.getPacketType() == PacketType.Play.Server.POSITION) {
                            packetEvent.setCancelled(true);
                        }
                    }
                } else if (packetEvent.getPacketType().equals(PacketType.Play.Server.MAP_CHUNK)) {
                    WrapperPlayServerMapChunk map = new WrapperPlayServerMapChunk(packetEvent.getPacket());
                    int x = map.getChunkX() * 16;
                    int z = map.getChunkZ() * 16;
                    Player player = packetEvent.getPlayer();
                    Bukkit.getScheduler().runTask(PluginCore.plugin, () -> {
                        if (PluginCore.worldEditM.isAvailable(player.getLocation().getWorld().getChunkAt(new Location(player.getLocation().getWorld(), x, 10, z)), player)) {
                            Bukkit.getScheduler().runTaskLater(PluginCore.plugin, () -> {
                                PluginCore.worldEditM.chunk(player.getLocation().getWorld().getChunkAt(new Location(player.getLocation().getWorld(), x, 10, z)), player);
                            }, 10);
                        }
                    });
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                if (packetEvent.getPacketType().equals(PacketType.Play.Client.SPECTATE)) {
                    if (UtilMethods.IN_SPECTATOR.contains(packetEvent.getPlayer().getName())) {
                        packetEvent.setCancelled(true);
                    }
                }
                if (ProtocolUtil.FREEZE_LOCATION.containsKey(packetEvent.getPlayer())) {
                    Location loc = ProtocolUtil.FREEZE_LOCATION.get(packetEvent.getPlayer());
                    if (packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
                        WrapperPlayClientPositionLook packet = new WrapperPlayClientPositionLook(packetEvent.getPacket());
                        packet.setX(loc.getX());
                        packet.setY(loc.getY());
                        packet.setZ(loc.getZ());
                        packet.setPitch(loc.getPitch());
                        packet.setYaw(loc.getYaw());
                    } else if (packetEvent.getPacketType() == PacketType.Play.Client.POSITION) {
                        WrapperPlayerClientPosition packet = new WrapperPlayerClientPosition(packetEvent.getPacket());
                        packet.setX(loc.getX());
                        packet.setY(loc.getY());
                        packet.setZ(loc.getZ());
                    }
                }
            }

            @Override
            public ListeningWhitelist getSendingWhitelist() {
                return ListeningWhitelist.newBuilder().normal().types(PacketType.Play.Server.POSITION, PacketType.Play.Server.MAP_CHUNK).build();
            }

            @Override
            public ListeningWhitelist getReceivingWhitelist() {
                return ListeningWhitelist.newBuilder().high().types(PacketType.Play.Client.SPECTATE, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION).build();
            }

            @Override
            public Plugin getPlugin() {
                return PluginCore.plugin;
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Map<Chunk, Set<Pair<String, Boolean>>> map = PluginCore.configM.getPhantomSchematics().getSchematics(event.getPlayer().getUniqueId());
        for (Chunk chunk : map.keySet()) {
            for (Pair<String, Boolean> pair : map.get(chunk)) {
                PluginCore.worldEditM.addPhantomBlocksNoConfig(chunk, event.getPlayer().getUniqueId(), pair.getKey(), pair.getValue());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        for (AbstractPlayer player : new ArrayList<>(AbstractPlayer.PLAYERS)) {
            if (player.getPlayer().equals(event.getPlayer())) {
                player.unregister();
            }
        }
        ProtocolUtil.TELEPORT_LOCATION.remove(event.getPlayer().getEntityId());
        ProtocolUtil.FREEZE_LOCATION.remove(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (UtilMethods.IN_SPECTATOR.contains(event.getPlayer().getName())) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
                event.setCancelled(true);
            }
        }
    }
}
