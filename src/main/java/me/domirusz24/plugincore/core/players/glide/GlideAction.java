package me.domirusz24.plugincore.core.players.glide;

import me.domirusz24.plugincore.core.stringobject.StringObject;
import me.domirusz24.plugincore.util.UtilMethods;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class GlideAction {

    public static GlideAction getFromPlayerAndArgs(Player player, List<String> args) {
        if (args.size() != 0) {
            if (GlideAction.getClassAction(args.get(0)) != null) {
                String loc = UtilMethods.locationToString(player.getLocation(), true);
                String[] arg = (String[]) ArrayUtils.addAll(new String[]{loc}, args.subList(1, args.size()).toArray());
                return GlideAction.getAction(args.get(0), arg);
            }
        }
        return null;
    }

    private static final HashMap<String, Class<? extends GlideAction>> CLASS_BY_NAME = new HashMap<>();

    static {
        CLASS_BY_NAME.put("glide", PlayerGlideAction.class);
        CLASS_BY_NAME.put("load", PlayerLoadAction.class);
        CLASS_BY_NAME.put("stop", PlayerStopAction.class);
        CLASS_BY_NAME.put("title", PlayerTitleAction.class);
        CLASS_BY_NAME.put("timeglide", PlayerGlideTimeAction.class);
        CLASS_BY_NAME.put("sfx", PlayerMusicPlayAction.class);
        CLASS_BY_NAME.put("blindness", PlayerBlindAction.class);
        CLASS_BY_NAME.put("command", PlayerCommandAction.class);
    }

    public static Set<String> getActions() {
        return CLASS_BY_NAME.keySet();
    }

    public static Class<? extends GlideAction> getClassAction(String name) {
        return CLASS_BY_NAME.getOrDefault(name, null);
    }

    public static GlideAction getAction(String name, List<String> args) {
        return StringObject.getInstance(CLASS_BY_NAME.getOrDefault(name, null), args);
    }

    public static GlideAction getAction(String name, String... args) {
        return getAction(name, Arrays.asList(args));
    }

    protected Location from;
    protected final Location to;

    public GlideAction(Location to){
        this.to = to;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public abstract String name();

    protected abstract ActionProgress run(Player player);

    public static abstract class ActionProgress {

        private int tick = 0;

        private boolean stopped = false;

        private final GlideAction action;

        private final Player player;

        protected ActionProgress(GlideAction action, Player player) {
            this.player = player;
            this.action = action;
        }

        public void tick() {
            if (isStopped()) {
                stop();
                return;
            }
            tick++;
            run();
        }

        public int getTick() {
            return tick;
        }

        public GlideAction getAction() {
            return action;
        }

        protected abstract void run();

        protected void stop() {
            stopped = true;
        }

        public boolean isStopped() {
            return stopped;
        }

        public Player getPlayer() {
            return player;
        }
    }


    public static class PreparedGlide {
        private final Location start;

        private final LinkedList<GlideAction> glideLocations = new LinkedList<>();

        public PreparedGlide(Location start) {
            this.start = start;
        }

        public Location getStart() {
            return start.clone();
        }

        public boolean isEmpty() {
            return glideLocations.isEmpty();
        }

        public Queue<GlideAction> getGlideLocations() {
            return new LinkedList<>(glideLocations);
        }

        private boolean teleportToNPC = true;

        public void setTeleportToNPC(boolean npc) {
            this.teleportToNPC = npc;
        }

        public boolean teleportToNPC() {
            return teleportToNPC;
        }

        public PreparedGlide addAction(GlideAction action) {
            if (glideLocations.isEmpty()) {
                action.setFrom(start);
            } else {
                action.setFrom(glideLocations.peekLast().getTo());
            }
            glideLocations.add(action);
            return this;
        }
    }
}
