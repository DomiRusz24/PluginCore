package me.domirusz24.plugincore.util;

import com.comphenix.protocol.events.PacketContainer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.domirusz24.plugincore.PluginCore;
import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.plugincore.config.AbstractConfig;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilMethods {

    private static final Map<String, ItemStack> SKULL_CACHE = new HashMap<>();
    private static final Map<String, ItemStack> TEXTURE_CACHE = new HashMap<>();

    public static final Set<String> IN_SPECTATOR = new HashSet<>();

    public static final Map<Character, Integer> FONT_SIZES;
    public static final int DEFAULT_CHAR_WIDTH = 6;

    static {
        FONT_SIZES = Stream.of(new Object[][]{
                {' ', 4}, {'!', 2}, {'"', 5}, {'#', 6}, {'$', 6}, {'%', 6}, {'&', 6}, {'\'', 3},
                {'(', 6}, {')', 6}, {'*', 5}, {'+', 6}, {',', 2}, {'-', 6}, {'.', 2}, {'/', 6},
                {'0', 6}, {'1', 6}, {'2', 6}, {'3', 6}, {'4', 6}, {'5', 6}, {'6', 6}, {'7', 6},
                {'8', 6}, {'9', 6}, {':', 2}, {';', 2}, {'<', 5}, {'=', 6}, {'>', 5}, {'?', 6},
                {'@', 7}, {'A', 6}, {'B', 6}, {'C', 6}, {'D', 6}, {'E', 6}, {'F', 6}, {'G', 6},
                {'H', 6}, {'I', 4}, {'J', 6}, {'K', 6}, {'L', 6}, {'M', 6}, {'N', 6}, {'O', 6},
                {'P', 6}, {'Q', 6}, {'R', 6}, {'S', 6}, {'T', 6}, {'U', 6}, {'V', 6}, {'W', 6},
                {'X', 6}, {'Y', 6}, {'Z', 6}, {'[', 4}, {'\\', 6}, {']', 4}, {'^', 6}, {'_', 6},
                {'`', 3}, {'a', 6}, {'b', 6}, {'c', 6}, {'d', 6}, {'e', 6}, {'f', 5}, {'g', 6},
                {'h', 6}, {'i', 2}, {'j', 6}, {'k', 5}, {'l', 3}, {'m', 6}, {'n', 6}, {'o', 6},
                {'p', 6}, {'q', 6}, {'r', 6}, {'s', 6}, {'t', 4}, {'u', 6}, {'v', 6}, {'w', 6},
                {'x', 6}, {'y', 6}, {'z', 6}, {'{', 5}, {'|', 2}, {'}', 5}, {'~', 7}
        }).collect(Collectors.toMap(data -> (Character) data[0], data -> (Integer) data[1]));
    }

    public static void setSpectatorMode(Player player) {
        IN_SPECTATOR.add(player.getName());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public static void removeSpectatorMode(Player player) {
        IN_SPECTATOR.remove(player.getName());
    }

    public static void copyURLToFile(URL url, File file) {

        try {
            InputStream input = url.openStream();
            if (file.exists()) {
                if (file.isDirectory())
                    throw new IOException("File '" + file + "' is a directory");

                if (!file.canWrite())
                    throw new IOException("File '" + file + "' cannot be written");
            } else {
                File parent = file.getParentFile();
                if ((parent != null) && (!parent.exists()) && (!parent.mkdirs())) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }

            input.close();
            output.close();

            System.out.println("File '" + file + "' downloaded successfully!");
        }
        catch(IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public static String locationToString(Location location, boolean world) {
        if (world) {
            return location.getWorld().getName() + ","
                    + location.getBlockX() + ","
                    + location.getBlockY() + ","
                    + location.getBlockZ() + ","
                    + Math.round(location.getYaw()) + ","
                    + Math.round(location.getPitch());
        } else {
            return location.getBlockX() + ","
                    + location.getBlockY() + ","
                    + location.getBlockZ() + ","
                    + Math.round(location.getYaw()) + ","
                    + Math.round(location.getPitch());
        }
    }

    public static Location stringToLocation(String location) {
        String[] split = location.split(",");
        if (split.length == 6) {
            return new Location(AbstractConfig.getWorld(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]),
                    Integer.parseInt(split[5]));
        } else {
            return null;
        }
    }

    public static Location stringToLocation(String location, World world) {
        String[] split = location.split(",");
        if (split.length == 5) {
            return new Location(world,
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]));
        } else {
            return null;
        }
    }

    public static ItemStack isInPlayerHeadCache(String name) {
        if (SKULL_CACHE.containsKey(name)) {
            return SKULL_CACHE.get(name).clone();
        } else {
            return null;
        }
    }

    public static void getPlayerHead(String name, Consumer<ItemStack> onComplete) {
        final ItemStack skull;
        if (SKULL_CACHE.containsKey(name)) {
            skull = SKULL_CACHE.get(name);
            onComplete.accept(skull.clone());
        } else {
            skull = new ItemStack(Material.PLAYER_HEAD);
            skull.setDurability((short) 3);
            Bukkit.getScheduler().runTaskAsynchronously(PluginCore.plugin, () -> {
                try {
                    skull.setItemMeta(updateSkullMeta(name, (SkullMeta) skull.getItemMeta()));
                    Bukkit.getScheduler().runTask(PluginCore.plugin, () -> {
                        SKULL_CACHE.put(name, skull);
                        onComplete.accept(skull.clone());
                    });
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                    PluginCore.plugin.log(Level.FINE, "Could not load skull for player " + name + "!");
                }
            });
        }
    }

    public static void getTextureHead(String texture, Consumer<ItemStack> onComplete) {
        final ItemStack skull;
        texture = "http://textures.minecraft.net/texture/" + texture;
        if (TEXTURE_CACHE.containsKey(texture)) {
            skull = TEXTURE_CACHE.get(texture);
            onComplete.accept(skull.clone());
        } else {
            skull = new ItemStack(Material.PLAYER_HEAD);
            skull.setDurability((short) 3);
            String finalTexture = texture;
            Bukkit.getScheduler().runTaskAsynchronously(PluginCore.plugin, () -> {
                SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", finalTexture).getBytes());
                profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
                Field profileField = null;
                try {
                    assert skullMeta != null;
                    profileField = skullMeta.getClass().getDeclaredField("profile");
                }
                catch (NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                }
                assert profileField != null;
                profileField.setAccessible(true);
                try {
                    profileField.set(skullMeta, profile);
                }
                catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                skull.setItemMeta(skullMeta);

                Bukkit.getScheduler().runTask(PluginCore.plugin, () -> {
                    TEXTURE_CACHE.put(finalTexture, skull);
                    onComplete.accept(skull.clone());
                });
            });
        }
    }

    protected static SkullMeta updateSkullMeta(String name, SkullMeta meta) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called async!");
        }
        meta.setOwner(name);
        return meta;
    }

    public static String findColor(String prefix) {
        StringBuilder color = new StringBuilder();
        for (String s : prefix.split("§")) {
            color.append("§").append(s.charAt(0));
        }
        if (prefix.charAt(prefix.length() - 1) == '§') color.append('§');
        return color.toString();
    }


    public static String translateColor(String string) {
        if (string == null) return null;
        return ChatColor.translateAlternateColorCodes('&', string.replaceAll("\\Q|\\E\\Q|\\E", "\n&r"));
    }

    public static String[] wordWrap(final String rawString, final int lineLength, final String wrapPrefix) {
        // A null string is a single line
        if (rawString == null) {
            return new String[]{""};
        }

        final int maxWidth = lineLength * DEFAULT_CHAR_WIDTH;

        // A string shorter than the lineWidth is a single line
        if (getWidth(rawString) <= maxWidth && !rawString.contains("\n")) {
            return new String[]{rawString};
        }

        // Work out wrapPrefix color chars
        final int maxWrapWidth = maxWidth - getWidth(wrapPrefix);

        final char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        final List<String> lines = new LinkedList<>();
        int wordWidth = 0;
        int lineWidth = 0;

        for (int i = 0; i < rawChars.length; i++) {
            final char singleChar = rawChars[i];

            // skip chat color modifiers
            if (singleChar == ChatColor.COLOR_CHAR) {
                if (rawChars.length <= i + 1) {
                    break;
                }
                word.append(ChatColor.getByChar(String.valueOf(rawChars[i + 1]).toLowerCase(Locale.ROOT)));
                i++; // Eat the next character as we have already processed it
                continue;
            }

            final int width = getWidth(singleChar);

            if (singleChar != ' ' && singleChar != '\n') {
                // Extremely long word begins a line, break the word up
                if (line.length() == 0 && wordWidth + width >= (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                    lines.add(word.toString());
                    word = new StringBuilder();
                    wordWidth = 0;
                }

                // Word too long with rest of line, force line to wrap
                if (line.length() > 0 && lineWidth + wordWidth + width >= (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                    lineWidth = 0;
                }

                word.append(singleChar);
                wordWidth += width;
                continue;
            }

            if (singleChar == '\n') {
                // NewLine forces a new line
                line.append(' ');
                line.append(word);
                lines.add(line.toString());
                line = new StringBuilder();
                word = new StringBuilder();
                lineWidth = 0;
                continue;
            }

            if (line.length() > 0) {
                line.append(' ');
                lineWidth += getWidth(' ');
            }
            line.append(word);
            lineWidth += wordWidth;
            word = new StringBuilder();
            wordWidth = 0;
        }

        if (line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }

        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != ChatColor.COLOR_CHAR) {
            lines.set(0, ChatColor.WHITE + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i - 1);
            final String subLine = lines.get(i);

            //char color = pLine.charAt(pLine.lastIndexOf(ChatColor.COLOR_CHAR) + 1);
            lines.set(i, wrapPrefix + getLastColors(pLine) + subLine);
        }

        return lines.toArray(new String[0]);
    }

    public static int getWidth(final String input) {
        int ret = 0;
        final char[] rawChars = input.toCharArray();

        for (int i = 0; i < rawChars.length; i++) {
            if (rawChars[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            ret += getWidth(rawChars[i]);
        }
        return ret;
    }

    public static int getWidth(final Character character) {
        return FONT_SIZES.getOrDefault(character, DEFAULT_CHAR_WIDTH);
    }

    public static String getLastColors(final String input) {
        if (input == null) return null;
        ChatColor lastColor = null;
        final List<ChatColor> lastFormats = new ArrayList<>();

        final int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            final char section = input.charAt(index);
            if (section != 167 || index >= length - 1) {
                continue;
            }
            final char colorChar = input.charAt(index + 1);
            final ChatColor color = ChatColor.getByChar(colorChar);

            if (color != null) {
                if (color.equals(ChatColor.RESET)) {
                    break;
                }

                if (color.isColor() && lastColor == null) {
                    lastColor = color;
                    continue;
                }

                if (color.isFormat() && !lastFormats.contains(color)) {
                    lastFormats.add(color);
                }
            }
        }

        String result = lastFormats.stream()
                .map(ChatColor::toString)
                .collect(Collectors.joining(""));

        if (lastColor != null) {
            result = lastColor.toString() + result;
        }
        return result;
    }

    public static String[] stringToList(String string) {
        if (string == null) return null;
        return string.split("\\n");
    }

    public static String secondsToMinutes(int seconds) {
        int minutes = (int) ((double) seconds / 60);
        seconds = seconds - (minutes * 60);
        if ((float) seconds / 10f < 1f) {
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }


    public static List<String> getPossibleCompletions(List<String> args, List<String> possibilitiesOfCompletion) {
        String argumentToFindCompletionFor = args.get(args.size() - 1);
        ArrayList<String> listOfPossibleCompletions = new ArrayList<>();

        for (String foundString : possibilitiesOfCompletion) {
            if (foundString == null) continue;
            if (foundString.regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
                listOfPossibleCompletions.add(foundString);
            }
        }
        return listOfPossibleCompletions;
    }


    // Outputs all classes in com.PluginCore, really useful for annotations!
    public static List<Class<?>> findClasses() {
        List<Class<?>> classes = findClasses(PluginCore.plugin.packageName());
        if (classes == null) {
            classes = new ArrayList<>();
        }
        classes.addAll(findClasses("me.domirusz24.plugincore"));
        return classes;
    }

    public static List<Class<?>> findClasses(String path) {
        JavaPlugin plugin = PluginCore.plugin;
        ClassLoader loader = plugin.getClass().getClassLoader();
        path = path.replace('.', '/');
        JarFile jar = null;
        if (loader != null) {
            try {
                Enumeration<URL> resources = loader.getResources(path);
                String jarloc;
                try {
                    jarloc = resources.nextElement().getPath();
                } catch (NoSuchElementException error) {
                    error.printStackTrace();
                    return null;
                }
                jarloc = jarloc.substring(5, jarloc.length() - path.length() - 2);
                String s = URLDecoder.decode(jarloc, "UTF-8");
                jar = new JarFile(new File(s));
            } catch (IOException error) {
                error.printStackTrace();
                return null;
            }
        }
        if (jar == null) return null;
        ArrayList<Class<?>> classes = new ArrayList<>();
        Enumeration<?> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            if (entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                if (className.startsWith(path.replace('/', '.'))) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className, true, loader);
                        if (clazz != null) classes.add(clazz);
                    } catch (Error | Exception ignored) {}
                }
            }
        }
        return classes;
    }

    public static boolean hasScoreboard(Player player) {
        return !player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    // Outputs all files in a directory
    public static List<File> getFiles(File directory) {
        List<File> files = new ArrayList<>();
        if (directory == null) {
            return files;
        }
        if (directory.isFile()) {
            directory = directory.getParentFile();
        }
        File[] folderFiles = directory.listFiles();
        assert folderFiles != null;
        for (File file : folderFiles) {
            if (!file.isDirectory()) {
                files.add(file);
            }
        }
        return files;
    }

    public static List<File> getDirectories(File directory) {
        List<File> files = new ArrayList<>();
        if (directory == null) {
            return files;
        }
        if (directory.isFile()) {
            directory = directory.getParentFile();
        }
        File[] folderFiles = directory.listFiles();
        assert folderFiles != null;
        for (File file : folderFiles) {
            if (file.isDirectory()) {
                files.add(file);
            }
        }
        return files;
    }

    private static int getStage(int num, int stage1, int stage2) {
        return num > stage1 ? 1 : num > stage2 ? 2 : 3;
    }

    public static String getNumberPrefix(int num) {
        int stage = getStage(num, 5, 3);
        switch (stage) {
            case 1: return ChatColor.GREEN + "";
            case 2: return ChatColor.YELLOW + "";
            default: return ChatColor.RED + "" + ChatColor.BOLD + "";
        }
    }

    public static String getPercentPrefix(int num) {
        int stage = getStage(num, 66, 33);
        switch (stage) {
            case 1: return ChatColor.GREEN + "";
            case 2: return ChatColor.YELLOW + "";
            default: return ChatColor.RED + "" + ChatColor.BOLD + "";
        }
    }

    public static BarColor getBarColorFromPercent(int num) {
        int stage = getStage(num, 66, 33);
        switch (stage) {
            case 1: return BarColor.GREEN;
            case 2: return BarColor.YELLOW;
            default: return BarColor.RED;
        }
    }

    public static ItemStack createItem(Material type, byte data, String name, boolean glow, String... desc) {
        ItemStack is = new ItemStack(type, 1);
        is.setDurability(data);
        ItemMeta meta = is.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (desc.length != 0) meta.setLore(Arrays.asList(desc));
        meta.setUnbreakable(true);
        if (glow) {
            meta.addEnchant(Enchantment.LUCK, 1, false);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        is.setItemMeta(meta);
        return is;
    }

    public static void sendToAllPlayers(PacketContainer packetContainer) {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            try {
                PluginCore.protocol.sendServerPacket(p, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }


}
