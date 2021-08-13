package me.domirusz24.plugincore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import me.domirusz24.plugincore.core.players.PlayerData;
import me.domirusz24.plugincore.managers.*;
import me.domirusz24.plugincore.managers.database.DataBaseManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.domirusz24.plugincore.managers.database.DataBaseTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

public abstract class PluginCore extends JavaPlugin {

    /*
    depend: [ProtocolLib, WorldEdit, PlaceholderAPI]
softdepend: [Multiverse-Core]
     */

    // Plugin
    public static PluginCore plugin;

    // Dependencies
    public static WorldEditPlugin worldEdit = null;
    public static MultiverseCore multiverse = null;
    public static ProtocolManager protocol = null;


    // Managers
    public static DataBaseManager SqlM;
    public static ConfigManager configM;
    public static CommandManager commandM;
    public static GUIManager guiM;
    public static RegionManager regionM;
    public static WorldEditManager worldEditM;
    public static ChatGUIManager chatGuiM;
    public static ScoreboardManager boardM;
    public static me.domirusz24.plugincore.managers.ProtocolManager nmsM;
    public static SignManager signM;
    public static PAPIManager papiM;
    public static PlayerDataManager playerDataM;

    @Override
    public void onEnable() {
        initialize();
        loadDependencies();
        loadManagers();
        registerEvents();
    }

    @Override
    public void onDisable() {
        disable();
    }

    // onEnable
    private void initialize() {
        plugin = this;
        log(Level.INFO, plugin.getName() + " " + plugin.getDescription().getVersion() + " has been loaded!");


        _initialize();
    }

    protected abstract void _initialize();


    // Plugin dependencies, everything external.
    private void loadDependencies() {
        // WorldEdit
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        // Protocol
        protocol = ProtocolLibrary.getProtocolManager();

        // Multiverse
        multiverse = (MultiverseCore) hookInto("Multiverse-Core");

        _loadDependencies();
    }

    protected abstract void _loadDependencies();

    protected Plugin hookInto(String pluginName) {
        Plugin jPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (jPlugin != null) {
            log(Level.INFO, "Hooked into " + pluginName + "!");
            return jPlugin;
        } else {
            return null;
        }
    }

    protected boolean isHookAble(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }


    // Managers that ease the use of Core classes and dependencies.

    protected abstract String databasePrefix();

    public abstract String packageName();

    public abstract DataBaseTable[] getTables();

    protected abstract PAPIManager papiManager();

    private void loadManagers() {

        regionM = new RegionManager(plugin);
        // Config
        configM = new ConfigManager(this);
        loadConfigs();

        // SQL
        SqlM = new DataBaseManager(plugin, databasePrefix());
        SqlM.initDatabase();

        // Command
        commandM = new CommandManager(plugin);

        papiM = papiManager();
        if (papiM != null) papiM.register();

        nmsM = new me.domirusz24.plugincore.managers.ProtocolManager(this, ProtocolLibrary.getProtocolManager());

        guiM = new GUIManager(plugin);

        chatGuiM = new ChatGUIManager(plugin);

        worldEditM = new WorldEditManager(plugin, worldEdit);

        playerDataM = new PlayerDataManager(plugin);

        boardM = new ScoreboardManager(plugin);

        signM = new SignManager(plugin);

        _loadManagers();
        _loadCommands();
    }

    protected abstract void loadConfigs();

    public abstract void sqlLoad();

    protected abstract void _loadManagers();

    protected abstract void _loadCommands();

    // Spigot events
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new CoreListener(), this);
        _registerEvents();
    }

    protected abstract void _registerEvents();

    // onDisable
    private void disable() {
        log(Level.INFO, plugin.getName() + " " + plugin.getDescription().getVersion() + " has been disabled!");
        _disable();
        SqlM.onDisable();
    }

    protected abstract void _disable();

    // Use this for printing out info to the console.
    public void log(Level level, String msg) {
        this.getLogger().log(level, msg);
    }

    // Use this when something critical to the functioning of the plugin isn't working.
    public void shutOffPlugin() {
        log(Level.SEVERE, "The plugin has been disabled due to a critical error!");
        _shutOffPlugin();
        setEnabled(false);
    }

    protected abstract void _shutOffPlugin();

    public abstract PlayerData registerPlayer(String name, UUID uuid);

    // Config stuff
    @Override
    public YamlConfiguration getConfig() {
        return configM.getConfig();
    }

    @Override
    public void saveConfig() {
        configM.getConfig().save();
    }

    @Override
    public void reloadConfig() {
        configM.getConfig().reload();
    }
}
