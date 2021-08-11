package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerBlindAction extends GlideAction {

    private final double time;

    public PlayerBlindAction(Location to, double time) {
        super(to);
        setFrom(to);
        this.time = time;
    }

    @Override
    public String name() {
        return "blindness";
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
                if (first) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) time, 10));
                    first = false;
                }
                player.teleport(to);
            }
        };
    }
}
