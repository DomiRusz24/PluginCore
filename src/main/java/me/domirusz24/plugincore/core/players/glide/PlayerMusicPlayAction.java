package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerMusicPlayAction extends GlideAction {

    private final Sound sound;

    public PlayerMusicPlayAction(Location to, String sound) {
        super(to);
        this.sound = Sound.valueOf(sound);
    }

    public Sound getSound() {
        return sound;
    }

    @Override
    public String name() {
        return "sfx";
    }

    @Override
    protected ActionProgress run(Player player) {
        return new ActionProgress(this, player) {
            @Override
            protected void run() {
                player.playSound(player.getLocation(), sound, 500.0f, 1f);
                stop();
            }
        };
    }
}
