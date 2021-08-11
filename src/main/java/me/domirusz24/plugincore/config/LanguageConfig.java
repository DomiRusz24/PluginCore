package me.domirusz24.plugincore.config;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.managers.ConfigManager;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

public class LanguageConfig extends AbstractConfig {

    private static final HashMap<Class<?>, List<Field>> ANNOTATIONS_BY_CLASS = new HashMap<>();

    public LanguageConfig(String path, PluginCore plugin, ConfigManager manager) {
        super(path, plugin, manager);
        registerAnnotations();
    }

    @Override
    public boolean reload() {
        boolean success = super.reload();
        if (success) {
            return reloadAnnotations();
        }
        return false;
    }

    private boolean registerAnnotations() {
        List<Class<?>> classes = UtilMethods.findClasses();
        if (classes == null) {
            PluginCore.plugin.log(Level.WARNING, "Failed getting all classes!");
            return false;
        }

        Language annotation;
        for (Class<?> clazz : classes) {
            ANNOTATIONS_BY_CLASS.put(clazz, new ArrayList<>());
            Iterator<Field> iterator;
            try {
                iterator = Arrays.asList(clazz.getDeclaredFields()).iterator();
            } catch (Exception e) {
                continue;
            }

            while (iterator.hasNext()) {
                Field field;
                try {
                    field = iterator.next();
                } catch (Exception e) {
                    continue;
                }
                if (field.isAnnotationPresent(Language.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        annotation = field.getAnnotation(Language.class);
                        ANNOTATIONS_BY_CLASS.get(clazz).add(field);
                        try {
                            addDefault(annotation.value(), field.get(null));
                            if (isString(annotation.value())) {
                                field.set(null, UtilMethods.translateColor((String) get(annotation.value())));
                            } else {
                                field.set(null, get(annotation.value()));
                            }
                        } catch (Exception e) {
                            PluginCore.plugin.log(Level.WARNING, "Error loading language annotations in " + field.getName() + ", in class " + clazz.getName());
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        return save();
    }

    private boolean reloadAnnotations() {
        boolean success = true;
        for (Class<?> clazz : ANNOTATIONS_BY_CLASS.keySet()) {
            for (Field field : ANNOTATIONS_BY_CLASS.get(clazz)) {
                try {
                    if (isString(field.getAnnotation(Language.class).value())) {
                        field.set(null, UtilMethods.translateColor((String) get(field.getAnnotation(Language.class).value())));
                    } else {
                        field.set(null, get(field.getAnnotation(Language.class).value()));
                    }
                } catch (Exception e) {
                    PluginCore.plugin.log(Level.WARNING, "Error reloading language annotations in " + field.getName() + ", in class " + clazz.getName());
                    e.printStackTrace();
                    success = false;
                }
            }
        }
        return success;
    }
}
