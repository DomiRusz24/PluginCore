package me.domirusz24.plugincore.config;

import com.onarandombox.MultiverseCore.MultiverseCore;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.configvalue.AbstractConfigValue;
import me.domirusz24.plugincore.core.PluginInstance;
import me.domirusz24.plugincore.core.displayable.CustomSign;
import me.domirusz24.plugincore.core.region.CustomRegion;
import me.domirusz24.plugincore.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

public abstract class AbstractConfig extends YamlConfiguration implements PluginInstance {

    private final File file;

    private final String name;

    protected final PluginCore plugin;

    protected final ConfigManager manager;

    private final ArrayList<AbstractConfigValue<?>> values = new ArrayList<>();

    public AbstractConfig(File file, PluginCore plugin, ConfigManager manager) {
        this.plugin = plugin;
        this.file = file;
        this.name = file.getName();
        this.manager = manager;
        setUp();
    }

    public AbstractConfig(String path, PluginCore plugin, ConfigManager manager) {
        this(new File(plugin.getDataFolder(), path), plugin, manager);
    }

    public AbstractConfig(String path, PluginCore plugin) {
        this(new File(plugin.getDataFolder(), path), plugin);
    }

    public AbstractConfig(File file, PluginCore plugin) {
        this(file, plugin, plugin.configM);
    }

    public void addValue(AbstractConfigValue<?> value) {
        values.add(value);
    }

    private void setUp() {
        if (autoGenerate()) {
            createFile();
        }
        options().copyDefaults(true);
        manager.registerConfig(this);
        reload();
    }

    private boolean createFile() {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                plugin.log(Level.INFO, "Created file directory for " + file.getPath() + ".");
            }
            try {
                file.createNewFile();
                plugin.log(Level.INFO, "Created file " + file.getPath() + ".");
            } catch (IOException error) {
                error.printStackTrace();
                plugin.log(Level.SEVERE, "Couldn't create file " + file.getPath() + "! Shutting off plugin...");
                plugin.shutOffPlugin();
                return false;
            }
        }
        return true;
    }

    private boolean reload(boolean publicReload) {
        if (!file.exists()) {
            if (autoGenerate()) {
                return createFile();
            } else {
                return true;
            }
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            plugin.log(Level.WARNING, "Couldn't reload " + name + " config!");
            return false;
        }
        _reload();
        if (publicReload) values.forEach(AbstractConfigValue::autoReload);
        return true;
    }

    protected boolean autoGenerate() {
        return false;
    }

    public boolean reload() {
        return reload(true);
    }

    public void _reload(){}

    public boolean save() {
        if (!file.exists()) {
            if (!createFile()) return false;
        }
        for (AbstractConfigValue<?> value : values) {
            value.saveDefault();
        }
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.log(Level.SEVERE, "Couldn't save " + name + " config!");
            return false;
        }
        return true;
    }

    @Override
    public void set(String path, Object object) {
        if (object instanceof Location) {
            setLocation(path, (Location) object);
        } else {
            super.set(path, object);
        }
    }

    public void setLocation(String path, Location location) {
        set(path + ".x", Math.floor(location.getX()));
        set(path + ".y", Math.floor(location.getY()));
        set(path + ".z", Math.floor(location.getZ()));
        set(path + ".yaw", location.getYaw());
        set(path + ".pitch", location.getPitch());
        set(path + ".world", location.getWorld().getName());
    }

    public Location getLocation(String path) {
        if (!contains(path + ".world")) {
            return null;
        }
        double x = getInt(path + ".x");
        double y = getInt(path + ".y");
        double z = getInt(path + ".z");
        double yaw = getDouble(path + ".yaw");
        double pitch = getDouble(path + ".pitch");
        String w = getString(path + ".world");
        World world = getWorld(w);
        if (world == null) {
            plugin.log(Level.WARNING, "World \"" + w + "\" doesn't exist!");
            return null;
        }
        return new Location(world, x + 0.5, y, z + 0.5, (float) yaw, (float) pitch);
    }

    public void setSign(String path, CustomSign hologram) {
        setLocation(path, hologram.getLocation());
    }

    public CustomSign getSign(String path, String ID) {
        CustomSign sign = plugin.signM.getSign(ID);
        if (sign == null) {
            sign = new CustomSign(plugin, ID);
            Location location = getLocation(path);
            if (location != null) {
                Block block = location.getBlock();
                if (block.getType().name().contains("SIGN")) {
                    sign.setSign((Sign) block.getState());
                }
            }
        }
        return sign;
    }

    public void setWESelection(String path, Location min, Location max) {
        if (min == null || max == null) {
            return;
        }
        set(path + ".world", min.getWorld().getName());
        for (int i = 0; i <= 1; i++) {
            Location location = i == 0 ? min : max;
            String minmax = i == 0 ? ".min" : ".max";
            set(path + minmax + ".x", Math.floor(location.getX()));
            set(path + minmax + ".y", Math.floor(location.getY()));
            set(path + minmax + ".z", Math.floor(location.getZ()));
        }
    }

    public Location[] getWESelection(String path) {
        if (!contains(path + ".world")) {
            return null;
        }
        Location min = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        Location max = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        String w = getString(path + ".world");
        World world = getWorld(w);
        for (int i = 0; i <= 1; i++) {
            String minmax = i == 0 ? ".min" : ".max";
            double x = getInt(path + minmax + ".x");
            double y = getInt(path + minmax + ".y");
            double z = getInt(path + minmax + ".z");
            if (i == 0) {
                min = new Location(world, x, y, z);
            } else {
                max = new Location(world, x, y, z);
            }
        }
        return new Location[]{min, max};
    }

    public void setRegion(String path, CustomRegion region) {
        if (region.getMin() != null) {
            setWESelection(path, region.getMin(), region.getMax());
        }
    }

    public<T extends CustomRegion> T getRegion(String path, String ID, Class<T> clazz) {
        T region;
        try {
            region = clazz.getConstructor(String.class).newInstance(ID);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        Location[] selection = getWESelection(path);
        if (selection != null) {
            region.setLocations(selection[0], selection[1]);
        }
        return region;
    }

    public static World getWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null && Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) {
            try {
                world = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager().getMVWorld(name).getCBWorld();
            } catch (NullPointerException e) {
                return null;
            }
        }
        return world;
    }

    public File getFile() {
        return file;
    }



    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
