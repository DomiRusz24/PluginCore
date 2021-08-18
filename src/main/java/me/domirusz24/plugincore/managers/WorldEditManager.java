package me.domirusz24.plugincore.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.Languages;
import me.domirusz24.plugincore.util.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WorldEditManager extends Manager {
    private final WorldEditPlugin worldEditPlugin;

    private final HashMap<Chunk, HashMap<UUID, Set<Pair<String, Boolean>>>> SCHEDULED_PHANTOM_BLOCKS = new HashMap<>();

    public List<String> getSchemtatics() {
        File folder = new File(plugin.getDataFolder(), "/schematics/");
        if (folder.listFiles() == null) return new ArrayList<>();
        return Arrays.stream(folder.listFiles()).map(File::getName).collect(Collectors.toList());
    }


    public WorldEditManager(PluginCore plugin, WorldEditPlugin worldEdit) {
        super(plugin);
        this.worldEditPlugin = worldEdit;
    }

    public Pair<Location, Location> getPlayerSelection(Player player) {
        try {
            Region region = worldEditPlugin.getSession(player).getSelection(worldEditPlugin.getSession(player).getSelectionWorld());
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            return new Pair<>(new Location(BukkitAdapter.adapt(region.getWorld()), min.getBlockX(), min.getBlockY(), min.getBlockZ()), new Location(BukkitAdapter.adapt(region.getWorld()), max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        } catch (IncompleteRegionException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveSchematic(CommandSender sender, Location min, Location max, File folder, String name) {
        CuboidRegion region = new CuboidRegion(Vector3.toBlockPoint(min.getX(), min.getY(), min.getZ()), Vector3.toBlockPoint(max.getX(), max.getY(), max.getZ()));
        region.setWorld(BukkitAdapter.adapt(min.getWorld()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1)) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, region, clipboard, region.getMinimumPoint()
            );
            try {
                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }

        File file = new File(folder, name + ".schem");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSchematic(CommandSender sender, Location min, Location max, String name) {
        name = plugin.configM.getSchematicConfig().getNonDuplicateId(name);

        File folder = new File(plugin.getDataFolder(), "/schematics/");

        plugin.configM.getSchematicConfig().setMin(name, min);

        saveSchematic(sender, min, max, folder, name);
    }

    public boolean getSchematic(CommandSender sender, Location location, File folder, String name) {

        Clipboard clipboard = getClipboard(sender, location, folder, name);

        if (clipboard == null) return false;


        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            try {
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
        if (sender != null) sender.sendMessage(Languages.SUCCESS);
        return true;
    }

    public boolean getSchematic(CommandSender sender, String name) {
        Location min = plugin.configM.getSchematicConfig().getMin(name);
        if (min == null) return false;

        File folder = new File(plugin.getDataFolder(), "/schematics/");

        return getSchematic(sender, min, folder, name);
    }

    // PHANTOM

    public boolean showPhantomSchematic(Player player, String name, boolean autoRemove) {
        return showPhantomSchematic(player, name, autoRemove, true);
    }

    public boolean showPhantomSchematic(Player player, Location location, File folder, String name, boolean autoRemove) {
        return showPhantomSchematic(player, location, folder, name, autoRemove, true);
    }

    private boolean showPhantomSchematic(Player player, String name, boolean autoRemove, boolean save) {
        Location min = plugin.configM.getSchematicConfig().getMin(name);
        if (min == null) return false;

        File folder = new File(plugin.getDataFolder(), "/schematics/");

        return showPhantomSchematic(player, min, folder, name, autoRemove, save);
    }

    private boolean showPhantomSchematic(Player player, Location location, File folder, String name, boolean autoRemove, boolean save) {

        Clipboard clipboard = getClipboard(null, location, folder, name);

        if (clipboard == null) return false;

        BlockVector3 min = clipboard.getRegion().getMinimumPoint();
        BlockVector3 max = clipboard.getRegion().getMaximumPoint();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                    for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {

                            Chunk chunk = location.getWorld().getChunkAt(new Location(location.getWorld(), x, 10, z));

                            if (save) {
                                addPhantomBlocks(chunk, player.getUniqueId(), name, autoRemove);
                            }

                            if (chunk.isLoaded()) {
                                player.sendBlockChange(
                                        new Location(chunk.getWorld(), x, y, z),
                                        BukkitAdapter.adapt(clipboard.getFullBlock(BlockVector3.at(x, y, z))).getMaterial().createBlockData());
                                if (autoRemove) {
                                    SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(player.getUniqueId()).removeIf(single -> single.getKey().equals(name));
                                    plugin.configM.getPhantomSchematics().removeSchematic(chunk.getWorld(), chunk.getX(), chunk.getZ(), player.getUniqueId(), name);
                                }
                            }
                        }
                    }
                }
                plugin.configM.getPhantomSchematics().save();
            }
        }.runTaskAsynchronously(worldEditPlugin);

        return true;
    }

    private void addPhantomBlocks(Chunk chunk, UUID uuid, String name, boolean autoRemove) {
        if (!SCHEDULED_PHANTOM_BLOCKS.containsKey(chunk)) {
            SCHEDULED_PHANTOM_BLOCKS.put(chunk, new HashMap<>());
        }
        if (!SCHEDULED_PHANTOM_BLOCKS.get(chunk).containsKey(uuid)) {
            SCHEDULED_PHANTOM_BLOCKS.get(chunk).put(uuid, new HashSet<>());
        }
        SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(uuid).add(new Pair<>(name, autoRemove));
        for (Pair<String, Boolean> pair : new ArrayList<>(SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(uuid))) {
            if ((pair.getKey() + "_r").equalsIgnoreCase(name)) {
                SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(uuid).remove(pair);
                plugin.configM.getPhantomSchematics().removeSchematic(chunk.getWorld(), chunk.getX(), chunk.getZ(), uuid, name.substring(0, name.length() - 2));
            }
        }
        plugin.configM.getPhantomSchematics().addSchematic(chunk.getWorld(), chunk.getX(), chunk.getZ(), uuid, name, autoRemove);
    }

    public void addPhantomBlocksNoConfig(Chunk chunk, UUID uuid, String name, boolean autoRemove) {
        if (!SCHEDULED_PHANTOM_BLOCKS.containsKey(chunk)) {
            SCHEDULED_PHANTOM_BLOCKS.put(chunk, new HashMap<>());
        }
        if (!SCHEDULED_PHANTOM_BLOCKS.get(chunk).containsKey(uuid)) {
            SCHEDULED_PHANTOM_BLOCKS.get(chunk).put(uuid, new HashSet<>());
        }
        SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(uuid).add(new Pair<>(name, autoRemove));
    }

    public boolean isAvailable(Chunk chunk, Player player) {
        if (SCHEDULED_PHANTOM_BLOCKS.containsKey(chunk)) {
            return SCHEDULED_PHANTOM_BLOCKS.get(chunk).containsKey(player.getUniqueId());
        }
        return false;
    }

    public void chunk(Chunk chunk, Player player) {
        if (chunk.isLoaded() && SCHEDULED_PHANTOM_BLOCKS.containsKey(chunk)) {
            for (Pair<String, Boolean> pair : new ArrayList<>(SCHEDULED_PHANTOM_BLOCKS.get(chunk).get(player.getUniqueId()))) {
                showPhantomSchematic(player, pair.getKey(), pair.getValue(), false);
            }
        }
    }

    public Clipboard getClipboard(CommandSender sender, Location location, File folder, String name) {
        if (location == null) {
            if (sender != null) sender.sendMessage(Languages.FAIL + " (Location null)");
            return null;
        }
        File file = new File(folder, name + ".schem");

        if (!file.exists()) {
            if (sender != null) sender.sendMessage(Languages.FAIL + " (File doesn't exist)");
            return null;
        }
        ClipboardReader reader;
        try {
            reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Clipboard clipboard;
        try {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ScheduledBlock {

        private final Vector location;
        private final BaseBlock block;

        private ScheduledBlock(Vector vector, BaseBlock block) {
            this.location = vector;
            this.block = block;
        }

        public Vector getLocation() {
            return location;
        }

        public BaseBlock getBlock() {
            return block;
        }
    }
}
