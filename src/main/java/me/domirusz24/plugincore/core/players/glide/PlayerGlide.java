package me.domirusz24.plugincore.core.players.glide;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.core.protocol.ProtocolUtil;
import me.domirusz24.plugincore.util.CompleteListener;
import me.domirusz24.plugincore.util.PerTick;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerGlide extends AbstractPlayer implements CompleteListener, PerTick {

    public static final HashSet<String> inGlide = new HashSet<>();

    private Location start;

    private final boolean teleportToOrigin;

    public PlayerGlide(PluginCore plugin, Player player, Location start, Queue<GlideAction> glide, boolean teleportToOrigin) {
        super(plugin, player);
        this.start = start.clone();
        this.teleportToOrigin = teleportToOrigin;
        glideLocations.add(new PlayerLoadAction(start, 60).run(player));
        for (GlideAction action : glide) {
            glideLocations.add(action.run(player));
        }
    }

    private Runnable finish = () -> {};

    public void onFinish(Runnable finish) {
        this.finish = finish;
    }

    private boolean started = false;

    private GameMode gameMode;
    private Location origin;
    private boolean canFly;
    private boolean hadFlight;

    @Language("PlayerGlide.Wait")
    public static String LANG_START = "&7Ładowanie terenu...";

    private final Queue<GlideAction.ActionProgress> glideLocations = new LinkedList<>();



    public void start() {
        if (!glideLocations.isEmpty() && !started) {
            started = true;
            inGlide.add(player.getName());
            gameMode = player.getGameMode();
            origin = player.getLocation().clone();
            canFly = player.getAllowFlight();
            hadFlight = player.isFlying();
            player.setAllowFlight(true);
            player.setFlying(true);
            flySpeed = player.getFlySpeed();
            ProtocolUtil.setGameMode(player, GameMode.SPECTATOR);
            player.setGameMode(GameMode.SPECTATOR);
            ProtocolUtil.teleport(player, start, player);
            player.setFlySpeed(0);
            registerListener();
            registerPerTick();
        }
    }

    public boolean isEmpty() {
        return glideLocations.isEmpty();
    }

    private int leftTicks = 0;

    private Location playerLocation;

    private GlideAction.ActionProgress current = null;

    @Override
    public void onTick() {
        if (current == null) {
            current = glideLocations.poll();
        }
        if (glideLocations.isEmpty() && current.isStopped()) {
            unregister();
        } else {
            if (current.isStopped()) {
                current = glideLocations.poll();
            }
            current.tick();
        }
    }

    public boolean teleportToOrigin() {
        return teleportToOrigin;
    }

    private boolean stopped = false;

    private float flySpeed = 0.2f;

    public void stop() {
        if (started) {
            ProtocolUtil.unfreeze(player);
            unregisterPerTick();
            stopped = true;
            unregisterListener();
            UtilMethods.removeSpectatorMode(player);
            ProtocolUtil.setGameMode(player, gameMode);
            if (teleportToOrigin) player.teleport(origin);
            if (!canFly) player.setFlying(false);
            player.setAllowFlight(canFly);
            if (canFly) player.setFlying(hadFlight);
            player.setFlySpeed(flySpeed);
            inGlide.remove(player.getName());
            finish.run();
        }
    }



    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(player)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onUnregister() {
        stop();
    }

    @Override
    public boolean resetInventory() {
        return false;
    }
}