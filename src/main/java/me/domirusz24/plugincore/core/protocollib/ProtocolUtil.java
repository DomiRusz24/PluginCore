package me.domirusz24.plugincore.core.protocollib;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayServerEntityTeleport;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayerServerGameStateChange;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ProtocolUtil {

    public static final HashMap<Integer, Location> TELEPORT_LOCATION = new HashMap<>();

    public static final HashMap<Player, Location> FREEZE_LOCATION = new HashMap<>();

    public static void teleport(LivingEntity entity, Location location, Player... receivers) {
        entity.teleport(location);
        /*
        teleport(entity.getEntityId(), location, receivers);

         */
    }

    public static void freezeAt(Player player, Location location) {
        FREEZE_LOCATION.put(player, location);
    }

    public static void unfreeze(Player player) {
        FREEZE_LOCATION.remove(player);
    }

    public static void setGameMode(Player player, GameMode gameMode) {
        if (gameMode == GameMode.SPECTATOR) {
            UtilMethods.IN_SPECTATOR.add(player.getName());
        } else {
            UtilMethods.IN_SPECTATOR.remove(player.getName());
        }
        /*
        WrapperPlayerServerGameStateChange stateChange = new WrapperPlayerServerGameStateChange();
        stateChange.setReason((int) 3);
        stateChange.setValue((float) gameMode.getValue());
        try {
            PluginCore.protocol.sendServerPacket(player, stateChange.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
         */
        player.setGameMode(gameMode);
    }

    public static void teleport(ProtocolManager manager, int entityId, Location location, Player... receivers) {
        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
        teleport.setEntityID(entityId);
        teleport.setX(location.getX());
        teleport.setY(location.getY());
        teleport.setZ(location.getZ());
        teleport.setPitch(location.getPitch());
        teleport.setYaw(location.getYaw());
        teleport.setOnGround(false);
        try {
            for (Player receiver : receivers) {
                manager.sendServerPacket(receiver, teleport.getHandle());
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        TELEPORT_LOCATION.put(entityId, location);
    }
}
