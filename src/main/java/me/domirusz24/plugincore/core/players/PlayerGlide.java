package me.domirusz24.plugincore.core.players;

import me.domirusz24.plugincore.CoreListener;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.core.protocollib.ProtocolUtil;
import me.domirusz24.plugincore.util.CompleteListener;
import me.domirusz24.plugincore.util.PerTick;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerGlide extends AbstractPlayer implements CompleteListener, PerTick {

    private Location start;

    private final Queue<LocationSpeed> glideLocations;

    public PlayerGlide(Player player, PreparedGlide glide) {
        super(player);
        this.start = glide.getStart();
        this.glideLocations = glide.getGlideLocations();
    }

    private Runnable finish = () -> {};

    public void onFinish(Runnable finish) {
        this.finish = finish;
    }

    private boolean started = false;

    private GameMode gameMode;
    private Location origin;

    @Language("PlayerGlide.Wait")
    public static String LANG_START = "&7Ładowanie terenu...";

    public void start() {
        if (!glideLocations.isEmpty() && !started) {
            started = true;
            gameMode = player.getGameMode();
            origin = player.getLocation().clone();
            ProtocolUtil.setGameMode(player, GameMode.SPECTATOR);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 3));
            player.sendTitle("", LANG_START, 20, 20, 20);
            ProtocolUtil.teleport(player, start, player);
            Bukkit.getScheduler().runTaskLater(PluginCore.plugin, () -> {
                if (!stopped) {
                    registerListener();
                    CoreListener.hookInListener(this);
                }
            }, 60);
        }
    }

    public boolean isEmpty() {
        return glideLocations.isEmpty();
    }

    private int leftTicks = 0;

    private Location playerLocation;

    private LocationSpeed current;

    @Override
    public void onTick() {
        if (leftTicks == 0) {
            if (glideLocations.isEmpty()) {
                unregister();
            } else {
                current = glideLocations.poll();
                leftTicks = current.getTicks();
                playerLocation = current.getFrom();
                ProtocolUtil.teleport(player, playerLocation, player);
            }
        } else {
            playerLocation.add(current.getDirection());
            playerLocation.setYaw((float) (playerLocation.getYaw() + current.getYawTurn()));
            playerLocation.setPitch((float) (playerLocation.getPitch() + current.getPitchTurn()));
            ProtocolUtil.teleport(player, playerLocation, player);
            leftTicks--;
        }
    }

    private static int getBestTurn(float start, float finish) {
        if (((start) - (finish) + 360) % 360 > 180) {
            return 1;
        } else {
            return -1;
        }
    }

    private boolean stopped = false;

    public void stop() {
        if (started) {
            ProtocolUtil.unfreeze(player);
            CoreListener.removeListener(this);
            stopped = true;
            unregisterListener();
            ProtocolUtil.setGameMode(player, gameMode);
            UtilMethods.removeSpectatorMode(player);
            player.teleport(origin);
            player.setFlying(false);
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

    private static class LocationSpeed implements Cloneable {
        private final Location from;
        private final Location to;
        private final double speed;

        private final double distance;

        private final int ticks;
        private final double yawTurn;
        private final double pitchTurn;
        private final Vector direction;
        
        private LocationSpeed(Location from, Location to, double speed, double distance, int ticks, double yawTurn, double pitchTurn, Vector direction) {
            this.from = from;
            this.to = to;
            this.speed = speed;
            this.distance = distance;
            this.ticks = ticks;
            this.yawTurn = yawTurn;
            this.pitchTurn = pitchTurn;
            this.direction = direction;
        }

        private LocationSpeed(Location from, Location to, double speed) {
            this.to = to;
            this.from = from;
            this.speed = speed;
            this.distance = from.distance(to);
            this.ticks = (int) (distance / speed);
            this.direction = to.toVector().subtract(from.toVector()).normalize().multiply(speed);
            yawTurn = (Math.abs(from.getYaw() - to.getYaw())) * (float) getBestTurn(from.getYaw() + 180, to.getYaw() + 180) / (float) ticks;
            pitchTurn = (Math.abs(from.getPitch() - to.getPitch())) * (float) getBestTurn(from.getPitch() + 180, to.getPitch() + 180) / (float) ticks;
        }

        public double getSpeed() {
            return speed;
        }

        public double getDistance() {
            return distance;
        }

        public double getPitchTurn() {
            return pitchTurn;
        }

        public double getYawTurn() {
            return yawTurn;
        }

        public int getTicks() {
            return ticks;
        }

        public Location getFrom() {
            return from.clone();
        }

        public Location getTo() {
            return to.clone();
        }

        public Vector getDirection() {
            return direction.clone();
        }
    }

    public static class PreparedGlide {
        private final Location start;

        private final LinkedList<LocationSpeed> glideLocations = new LinkedList<>();

        public PreparedGlide(Location start) {
            this.start = start;
        }

        public Location getStart() {
            return start.clone();
        }

        public boolean isEmpty() {
            return glideLocations.isEmpty();
        }

        public Queue<LocationSpeed> getGlideLocations() {
            return new LinkedList<>(glideLocations);
        }

        public PreparedGlide addLocation(Location location, double speed) {
            if (glideLocations.isEmpty()) {
                glideLocations.add(new LocationSpeed(start, location, speed));
            } else {
                glideLocations.add(new LocationSpeed(glideLocations.getLast().getTo(), location, speed));
            }
            return this;
        }

        public boolean addLocation(String location) {
            String[] split = location.split(",");
            Location loc = split.length == 6 ? new Location(start.getWorld(), (double)Integer.parseInt(split[0]), (double)Integer.parseInt(split[1]), (double)Integer.parseInt(split[2]), (float)Integer.parseInt(split[3]), (float)Integer.parseInt(split[4])) : null;
            if (loc == null) {
                return false;
            }
            double speed;
            try {
                speed = Double.parseDouble(split[5]);
            } catch (NumberFormatException e) {
                return false;
            }
            addLocation(loc, speed);
            return true;
        }

    }
}
