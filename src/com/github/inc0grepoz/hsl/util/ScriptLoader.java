package com.github.inc0grepoz.hsl.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import com.github.inc0grepoz.hsl.handler.Handler;
import com.github.inc0grepoz.hsl.handler.MethodHandleEventExecutor;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ScriptExecutor;
import com.github.inc0grepoz.ltse.unit.UnitFunction;

@SuppressWarnings("unchecked")
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
                plugin.getLogger().log(Level.SEVERE, "Failed to load script " + scriptName
                        + " (" + fileName + "): " + t);
                continue;
            }

            for (String event: events.getKeys(false)) {
                eventClass = events.getString(event + ".event-class");
                function = events.getString(event + ".function");
                priority = EventPriority.valueOf(events.getString(event + ".priority", "NORMAL"));

                try {
                    registerHandler(script, eventClass, function, priority);
                } catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to register a handler from script "
                            + scriptName + " (" + fileName + "): " + t);
                }
            }
        }
    }

    public void unloadScripts() {
        executor.unloadAll();
        HandlerList.unregisterAll(plugin);
    }

    private void registerHandler(Script script, String eventClass, String function,
            EventPriority priority) throws Throwable {

        UnitFunction fn = script.getFunction(function, 1);

        if (fn == null) {
            throw new AssertionError("Missing function " + function);
        }

        Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(eventClass);
        Method method = Handler.class.getDeclaredMethods()[0];
        Bukkit.getPluginManager().registerEvent(clazz, new Handler<>(fn), priority,
                new MethodHandleEventExecutor(clazz, method), plugin, false);

        plugin.getLogger().info("Registered handler " + function + "(" + eventClass + ")");
    }

}
