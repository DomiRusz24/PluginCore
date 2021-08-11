package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerLoadAction extends GlideAction {

    private final double time;

    public PlayerLoadAction(Location to, double time) {
        super(to);
        setFrom(to);
        this.time = time;
    }

    @Override
    public String name() {
        return "load";
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
                    player.sendTitle("", PlayerGlide.LANG_START, 10, (int) time, 10);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) time, 10));
                    first = false;
                }
                player.teleport(to);
            }
        };
    }
}
