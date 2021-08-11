package me.domirusz24.plugincore.core.displayable;


import me.domirusz24.plugincore.CoreListener;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.displayable.interfaces.ClickableBlock;
import me.domirusz24.plugincore.core.displayable.interfaces.LeftClickable;
import me.domirusz24.plugincore.core.displayable.interfaces.RightClickable;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CustomSign extends Displayable implements ClickableBlock {

    private Location sign;

    private final String identification;

    public CustomSign(String identification, PlaceholderObject... objects) {
        super(objects);
        this.identification = identification;
    }

    public CustomSign(String identification) {
        super();
        this.identification = identification;
    }

    public void setSign(Sign sign) {
        if (sign == null) {
            CoreListener.removeListener((LeftClickable) this);
            CoreListener.removeListener((RightClickable) this);
            if (this.sign != null) {
                this.sign.getBlock().setType(Material.AIR);
                this.sign = null;
            }
        } else {
            if (this.sign != null) {
                resetLines();
            }
            CoreListener.hookInListener((LeftClickable) this);
            CoreListener.hookInListener((RightClickable) this);
            this.sign = sign.getLocation();
            update();
        }
    }

    private void resetLines() {
        Sign sign = (Sign) this.sign.getBlock().getState();
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
        Bukkit.getScheduler().runTaskLater(PluginCore.plugin, () -> sign.update(true), 1);
    }

    public void removeSign() {
        setSign(null);
    }

    @Override
    protected void onUpdate(ArrayList<String> values) {
        if (sign != null) {
            resetLines();
            Sign sign = (Sign) this.sign.getBlock().getState();
            for (int i = 0; i < values.size(); i++) {
                sign.setLine(i, values.get(i));
            }
            Bukkit.getScheduler().runTaskLater(PluginCore.plugin, () -> sign.update(true), 1);
        }
    }

    @Override
    public int getSize() {
        return 4;
    }

    public String getID() {
        return identification;
    }

    @Override
    public void setSize(int size) {}

    @Override
    public Location getLocation() {
        return sign;
    }

    private Consumer<Player> leftClick = (p) -> {};

    private Consumer<Player> rightClick = (p) -> {};

    public void setOnLeftClick(Consumer<Player> method) {
        if (method == null) {
            leftClick = (p) -> {};
        } else {
            leftClick = method;
        }
    }

    public void setOnRightClick(Consumer<Player> method) {
        if (method == null) {
            rightClick = (p) -> {};
        } else {
            rightClick = method;
        }
    }

    @Override
    public void onLeftClick(Player player) {
        leftClick.accept(player);
        update();
    }

    @Override
    public void onRightClick(Player player) {
        rightClick.accept(player);
        update();
    }
}
