package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerGlideAction extends GlideAction {

    private final double speed;

    private double distance;

    private int ticks;
    private float pitchTurn;
    private float yawTurn;
    private Vector direction;

    public PlayerGlideAction(Location to, double speed) {
        super(to);
        this.speed = speed;
    }

    @Override
    public void setFrom(Location from) {
        from.setDirection(from.getDirection());
        to.setDirection(to.getDirection());
        super.setFrom(from);
        this.distance = from.distance(to);
        this.direction = to.toVector().subtract(from.toVector()).normalize().multiply(speed);
        this.ticks = ((int) (distance / speed)) + 1;
        pitchTurn = (float) (Math.abs(from.getPitch() - to.getPitch()) * (float) getBestTurn(from.getPitch() + 180, to.getPitch() + 180)) / (float) ticks;
        yawTurn = (float) (Math.abs((Math.abs(from.getYaw()) % 180) - (Math.abs(to.getYaw()) % 180)) * (float) getBestTurn(from.getYaw(), to.getYaw())) / (float) ticks;
        //System.out.println("| Yaw: " + yawTurn + " | Pitch: " + pitchTurn + " | Tick: " + ticks + " | Distance: " + distance + " |");
    }

    private static int getBestTurn(float start, float finish) {
        if (start < finish) {
            if(start - finish < 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (start - finish < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public String name() {
        return "glide";
    }

    @Override
    protected ActionProgress run(Player player) {
        return new ActionProgress(this, player) {
            private final Location current = getFrom().clone();

            @Override
            protected void run() {
                if (getTick() > ticks) {
                    stop();
                    return;
                }
                current.add(direction);
                current.setYaw(current.getYaw() + yawTurn);
                current.setPitch(current.getPitch() + pitchTurn);
                try {
                    player.teleport(current);
                } catch (Exception ignored) {}
            }
        };
    }
}
