package me.domirusz24.plugincore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.domirusz24.plugincore.core.PluginInstance;
import me.domirusz24.plugincore.core.chatgui.ChatGUI;
import me.domirusz24.plugincore.core.displayable.CustomScoreboard;
import me.domirusz24.plugincore.core.displayable.interfaces.LeftClickable;
import me.domirusz24.plugincore.core.displayable.interfaces.RightClickable;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.core.players.PlayerData;
import me.domirusz24.plugincore.core.players.glide.PlayerGlide;
import me.domirusz24.plugincore.core.protocol.ProtocolUtil;
import me.domirusz24.plugincore.core.protocol.wrappers.*;
import me.domirusz24.plugincore.util.Pair;
import me.domirusz24.plugincore.util.PerTick;
import me.domirusz24.plugincore.util.UtilMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CoreListener implements Listener, PluginInstance {

    private int baseComponentIndex = -1;

    private final HashSet<PerTick> PER_TICKABLE = new HashSet<>();
    private final HashSet<LeftClickable> LEFT_CLICKABLES = new HashSet<>();
    private final HashSet<RightClickable> RIGHT_CLICKABLES = new HashSet<>();

    public void hookInListener(LeftClickable clickable) {
        LEFT_CLICKABLES.add(clickable);
    }

    public void hookInListener(RightClickable clickable) {
        RIGHT_CLICKABLES.add(clickable);
    }

    public void hookInListener(PerTick tickable) {
        PER_TICKABLE.add(tickable);
    }

    public void removeListener(LeftClickable clickable) {
        LEFT_CLICKABLES.remove(clickable);
    }

    public void removeListener(RightClickable clickable) {
        RIGHT_CLICKABLES.remove(clickable);
    }

    public void removeListener(PerTick tickable) {
        PER_TICKABLE.remove(tickable);
    }

    private final PluginCore plugin;

    public CoreListener(PluginCore plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PerTick perTick : new ArrayList<>(PER_TICKABLE)) {
                perTick.onTick();
            }
        }, 1, 1);

        this.plugin = plugin;

        plugin.protocol.addPacketListener(new PacketListener() {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {

                if (packetEvent.isCancelled()) {
                    return;
                }

                if (packetEvent.getPacketType().equals(PacketType.Play.Server.POSITION)) {
                    try {
                        if (ProtocolUtil.TELEPORT_LOCATION.containsKey(packetEvent.getPlayer().getEntityId())) {
                            Location loc = ProtocolUtil.TELEPORT_LOCATION.get(packetEvent.getPlayer().getEntityId());
                            if (packetEvent.getPacketType() == PacketType.Play.Server.POSITION) {
                                packetEvent.setCancelled(true);
                            }
                        }
                    } catch (Throwable ignored){}
                } else if (packetEvent.getPacketType().equals(PacketType.Play.Server.MAP_CHUNK)) {
                    WrapperPlayServerMapChunk map = new WrapperPlayServerMapChunk(packetEvent.getPacket());
                    int x = map.getChunkX() * 16;
                    int z = map.getChunkZ() * 16;
                    Player player = packetEvent.getPlayer();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            if (plugin.worldEditM.isAvailable(player.getLocation().getWorld().getChunkAt(new Location(player.getLocation().getWorld(), x, 10, z)), player)) {
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    plugin.worldEditM.chunk(player.getLocation().getWorld().getChunkAt(new Location(player.getLocation().getWorld(), x, 10, z)), player);
                                }, 10);
                            }
                        } catch (Throwable ignored){}
                    });
                } else if (packetEvent.getPacketType().equals(PacketType.Play.Server.CHAT)) {
                    final WrapperPlayServerChat chat = new WrapperPlayServerChat(packetEvent.getPacket());
                    if (chat.getChatType().equals(EnumWrappers.ChatType.GAME_INFO)) {
                        //TODO: Maybe?
                    } else {
                        ChatGUI panel = plugin.chatGuiM.getChatGUI(packetEvent.getPlayer());
                        if (panel == null) {
                            return;
                        }

                        final PacketContainer packet = packetEvent.getPacket();
                        if (baseComponentIndex == -1) {
                            if (packet.getModifier().read(1) instanceof BaseComponent[]) {
                                baseComponentIndex = 1;
                            } else {
                                baseComponentIndex = 2;
                            }
                        }
                        final BaseComponent[] components = (BaseComponent[]) packet.getModifier().read(baseComponentIndex);
                        if (components != null && components.length > 0 && ((TextComponent) components[0]).getText().contains(ChatGUI.PASS_THROUGH)) {
                            packet.getModifier().write(baseComponentIndex, Arrays.copyOfRange(components, 1, components.length));
                            packetEvent.setPacket(packet);
                            return;
                        }

                        // Else save message to replay later
                        packetEvent.setCancelled(true);
                        panel.addToCache(chat);
                    }
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                if (packetEvent.getPacketType().equals(PacketType.Play.Client.SPECTATE)) {
                    if (UtilMethods.IN_SPECTATOR.contains(packetEvent.getPlayer().getName())) {
                        packetEvent.setCancelled(true);
                    }
                } else if (packetEvent.getPacketType() == PacketType.Play.Client.POSITION || packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
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
                } else if (packetEvent.getPacketType() == PacketType.Play.Client.CHAT) {
                    try {
                        if (PlayerGlide.inGlide.contains(packetEvent.getPlayer().getName())) {
                            packetEvent.setCancelled(true);
                            return;
                        }
                        ChatGUI panel = plugin.chatGuiM.getChatGUI(packetEvent.getPlayer());
                        if (panel != null) {
                            WrapperPlayClientChat chat = new WrapperPlayClientChat(packetEvent.getPacket());
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                panel.onCommand(chat.getMessage());
                            });
                            packetEvent.setCancelled(true);
                        }
                    } catch (Throwable ignored){}
                } else if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    try {
                        WrapperPlayClientUseEntity wrapper = new WrapperPlayClientUseEntity(packetEvent.getPacket());
                        if (wrapper.getType().equals(EnumWrappers.EntityUseAction.INTERACT_AT)) {
                            Location blockLocation = new Location(packetEvent.getPlayer().getWorld(), wrapper.getTargetVector().getX(), wrapper.getTargetVector().getY(), wrapper.getTargetVector().getZ());
                            if (plugin.worldEditM.isAvailable(blockLocation.getChunk(), packetEvent.getPlayer())) {
                                packetEvent.setCancelled(true);
                            }
                        }
                    } catch (Throwable ignored){}
                } else if (packetEvent.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
                    WrapperPlayClientBlockDig wrapper = new WrapperPlayClientBlockDig(packetEvent.getPacket());
                    if (wrapper.getStatus().equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK)) {
                        Location blockLocation = new Location(packetEvent.getPlayer().getWorld(), wrapper.getLocation().getX(), wrapper.getLocation().getY(), wrapper.getLocation().getZ());
                        if (plugin.worldEditM.isAvailable(blockLocation.getChunk(), packetEvent.getPlayer())) {
                            packetEvent.setCancelled(true);
                            packetEvent.getPlayer().sendBlockChange(blockLocation, blockLocation.getBlock().getBlockData());
                        }
                    }
                }
            }

            @Override
            public ListeningWhitelist getSendingWhitelist() {
                return ListeningWhitelist.newBuilder().normal().types(PacketType.Play.Server.POSITION, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.CHAT).build();
            }

            @Override
            public ListeningWhitelist getReceivingWhitelist() {
                return ListeningWhitelist.newBuilder().high().types(PacketType.Play.Client.SPECTATE, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION, PacketType.Play.Client.CHAT, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.BLOCK_DIG).build();
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.playerDataM.exists(event.getPlayer().getUniqueId())) {
            PlayerData p = plugin.playerDataM.getPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId());
            if (p != null) {
                p.onJoin();
            }
        } else {
            plugin.playerDataM.getPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId());
        }
        Map<Chunk, Set<Pair<String, Boolean>>> map = plugin.configM.getPhantomSchematics().getSchematics(event.getPlayer().getUniqueId());
        for (Chunk chunk : map.keySet()) {
            for (Pair<String, Boolean> pair : map.get(chunk)) {
                plugin.worldEditM.addPhantomBlocksNoConfig(chunk, event.getPlayer().getUniqueId(), pair.getKey(), pair.getValue());
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
        PlayerData p = plugin.playerDataM.getPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId());
        if (p != null) {
            p.onLeave();
        }
        plugin.guiM.unregister(event.getPlayer());
        CustomScoreboard cs = plugin.boardM.get(event.getPlayer());
        if (cs != null) cs.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (UtilMethods.IN_SPECTATOR.contains(event.getPlayer().getName())) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            RIGHT_CLICKABLES.forEach(p -> {
                if (p.isRightClickedOn(event)) {
                    p.onRightClick(event.getPlayer());
                }
            });
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            LEFT_CLICKABLES.forEach(p -> {
                if (p.isLeftClickedOn(event)) {
                    p.onLeftClick(event.getPlayer());
                }
            });
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        RIGHT_CLICKABLES.forEach(p -> {
            if (p.isRightClickedOn(event)) {
                p.onRightClick(event.getPlayer());
            }
        });
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
