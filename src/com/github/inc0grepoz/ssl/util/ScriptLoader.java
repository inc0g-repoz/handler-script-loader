package com.github.inc0grepoz.ssl.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ScriptExecutor;
import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ssl.handler.Handler;
import com.github.inc0grepoz.ssl.handler.MethodHandleEventExecutor;

public class ScriptLoader {

    private final ScriptExecutor executor = new ScriptExecutor();
    private final Plugin plugin;

    public ScriptLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadScripts() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection scripts = config.getConfigurationSection("scripts");
        ConfigurationSection events;

        String fileName, eventClass, function;
        EventPriority priority;
        Script script;

        for (String scriptName: scripts.getKeys(false)) {
            fileName = scripts.getString(scriptName + ".file");
            events = scripts.getConfigurationSection(scriptName + ".events");

            plugin.getLogger().info("Loading events from " + fileName);

            try (FileReader reader = new FileReader(new File(plugin.getDataFolder(), fileName))) {
                script = executor.load(reader);
            } catch (Throwable t) {
                plugin.getLogger().warning("Failed to load script " + scriptName
                        + " (" + fileName + "): " + t);
                continue;
            }

            for (String event: events.getKeys(false)) {
                eventClass = events.getString(event + ".event-class");
                function = events.getString(event + ".function");
                priority = EventPriority.valueOf(events.getString(event + ".priority", "NORMAL"));

                try {
                    registerHandler(script, scriptName, fileName, eventClass, function, priority);
                } catch (Throwable t) {
                    plugin.getLogger().warning("Failed to register a handler from script "
                            + scriptName + " (" + fileName + "): " + t);
                }
            }
        }
    }

    public void unloadScripts() {
        executor.unloadAll();
        HandlerList.unregisterAll(plugin);
    }

    @SuppressWarnings("unchecked")
    private void registerHandler(Script script, String scriptName,
            String fileName, String eventClass, String function,
            EventPriority priority) throws Throwable {
        UnitFunction fn = script.getFunction(function, 1);
        Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(eventClass);
        Method method = Handler.class.getDeclaredMethods()[0];
        Bukkit.getPluginManager().registerEvent(clazz, new Handler<>(fn), priority,
                generateExecutor(clazz, method), plugin, false);
        plugin.getLogger().info("Registered handler " + function + "(" + eventClass + ")");
    }

    private EventExecutor generateExecutor(Class<? extends Event> clazz, Method method) {
        return new MethodHandleEventExecutor(clazz, method);
    }

}
