package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerStopAction extends GlideAction {

    private final double time;

    public PlayerStopAction(Location to, double time) {
        super(to);
        setFrom(to);
        this.time = time;
    }

    @Override
    public String name() {
        return "stop";
    }

    @Override
    protected ActionProgress run(Player player) {
        return new ActionProgress(this, player) {

            private boolean first = true;

            @Override
            protected void run() {
                if (getTick() > (int) time) {
                    stop();
                    return;
                }
                player.teleport(to);
            }
        };
    }
}
