package me.domirusz24.plugincore.core.players.glide;

import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerTitleAction extends GlideAction {

    private final String title;
    private final String subTitle;
    private final int duration;

    public PlayerTitleAction(Location to, String title, String subTitle, int duration) {
        super(to);
        this.title = UtilMethods.translateColor(title).replaceAll("_", " ");
        this.subTitle = UtilMethods.translateColor(subTitle).replaceAll("_", " ");
        this.duration = duration;
    }

    @Override
    public String name() {
        return "title";
    }

    @Override
    protected ActionProgress run(Player player) {
        return new ActionProgress(this, player) {
            @Override
            protected void run() {
                player.sendTitle(title, subTitle, 10, duration, 10);
                stop();
            }
        };
    }
}
