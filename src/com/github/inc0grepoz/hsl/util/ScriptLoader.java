package com.github.inc0grepoz.hsl.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;

import com.github.inc0grepoz.hsl.SpigotPlugin;
import com.github.inc0grepoz.hsl.event.ScriptUnloadEvent;
import com.github.inc0grepoz.hsl.handler.MappedCommand;
import com.github.inc0grepoz.hsl.handler.MappedListener;
import com.github.inc0grepoz.hsl.handler.MethodHandleEventExecutor;
import com.github.inc0grepoz.hsl.util.proxy.IScript;
import com.github.inc0grepoz.hsl.util.proxy.IScriptExecutor;
import com.github.inc0grepoz.hsl.util.proxy.IUnitFunction;

/**
 * Represents a storage for plugin managed LIX4J scripts.
 * 
 * @author inc0g-repoz
 */
@SuppressWarnings("unchecked")
public class ScriptLoader {

    private final IScriptExecutor executor;
    private final Set<Command> commands = new HashSet<>();
    private final CommandMapProvider commandMap = new CommandMapProvider();
    private final SpigotPlugin plugin;

    /**
     * Creates a new loader for the specified instance of plugin.
     * 
     * @param plugin a plugin instance
     */
    public ScriptLoader(SpigotPlugin plugin, IScriptExecutor executor) {
        this.plugin = plugin;
        this.executor = (IScriptExecutor) executor;
    }

    /**
     * Only used once the plugin is enabled to indicate that the
     * scripts directory can be selected for LIX4J.
     */
    public void initLoaderDirectory()
    {
        File loaderDirectory = new File(plugin.getDataFolder(), "scripts");
        executor.setLoaderDirectory(loaderDirectory);
    }

    /**
     * Loads all scripts and maps functions to events and commands.
     */
    public void loadScripts() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection scripts = config.getConfigurationSection("scripts");
        ConfigurationSection commands, events;

        // Temp common
        String fileName;
        IScript script;

        for (String scriptName: scripts.getKeys(false)) {
            if (!scripts.getBoolean(scriptName + ".enabled")) {
                continue;
            }

            fileName = scripts.getString(scriptName + ".file");

            try (FileReader reader = new FileReader(new File(plugin.getDataFolder(), fileName))) {
                script = IScript.of(executor.load(reader));
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load script " + scriptName
                        + " (" + fileName + "): " + t);
                continue;
            }

            events = scripts.getConfigurationSection(scriptName + ".events");
            commands = scripts.getConfigurationSection(scriptName + ".commands");

            loadScriptEvents(fileName, scriptName, script, events);
            loadScriptCommands(fileName, scriptName, script, commands);
        }
    }

    // Only loads and maps events from a script
    private void loadScriptEvents(String fileName, String scriptName, IScript script,
            ConfigurationSection section) {

        Set<String> keys = section.getKeys(false);

        if (keys.isEmpty()) {
            return;
        }

        plugin.getLogger().info("Loading events from " + fileName);

        String eventClass, function;
        EventPriority priority;

        for (String event: keys) {
            eventClass = section.getString(event + ".event-class");
            function   = section.getString(event + ".function");
            priority   = EventPriority.valueOf(section.getString(event + ".priority", "NORMAL"));

            try {
                registerHandler(script, function, eventClass, priority);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to register a handler from script "
                        + scriptName + " (" + fileName + "): " + t);
            }
        }
    }

    // Creates and registers a listener for an event
    private void registerHandler(IScript script, String function, String eventClass,
            EventPriority priority) throws Throwable {

        IUnitFunction fn = IUnitFunction.of(script.getFunction(function, 1));

        if (fn == null) {
            throw new AssertionError("Missing function " + function);
        }

        Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(eventClass);
        Method method = MappedListener.class.getDeclaredMethods()[0];
        Bukkit.getPluginManager().registerEvent(clazz, new MappedListener<>(fn), priority,
                new MethodHandleEventExecutor(clazz, method), plugin, false);

        plugin.getLogger().info("Registered handler " + function + "(" + eventClass + ")");
    }

    // Only loads and maps commands from a script
    private void loadScriptCommands(String fileName, String scriptName, IScript script,
            ConfigurationSection section) {

        Set<String> keys = section.getKeys(false);

        if (keys.isEmpty()) {
            return;
        }

        plugin.getLogger().info("Loading commands from " + fileName);

        String description, permission, usage, fnExe, fnTab;
        List<String> aliases;

        for (String command: keys) {
            description = section.getString    (command + ".description");
            permission  = section.getString    (command + ".permission");
            usage       = section.getString    (command + ".usage");
            aliases     = section.getStringList(command + ".aliases");
            fnExe       = section.getString    (command + ".function-exe");
            fnTab       = section.getString    (command + ".function-tab");

            try {
                registerCommand(script, fnExe, fnTab, command, description, permission, usage, aliases);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to register a command from script "
                        + scriptName + " (" + fileName + "): " + t);
            }
        }
    }

    // Registers and maps a single command from a script
    private void registerCommand(IScript script, String fnExe, String fnTab,
            String name, String description, String permission, String usage,
            List<String> aliases) {

        Command command = new MappedCommand(script, fnExe, fnTab, name,
                description, permission, usage, aliases);

        if (!commands.add(command)) {
            throw new RuntimeException("Duplicate command /" + name);
        }

        Map<String, Command> knownCommands = commandMap.getKnownCommands();
        knownCommands.put(name, command);
        command.getAliases().forEach(alias -> knownCommands.put(alias, command));

        plugin.getLogger().info("Mapped command /" + name + " to " + fnExe);
    }

    /**
     * Unloads all scripts with all of their handlers.
     */
    public void unloadScripts() {
        Bukkit.getPluginManager().callEvent(new ScriptUnloadEvent());
        executor.unloadAll();
        unregisterCommands();
        HandlerList.unregisterAll(plugin);
    }

    // Unregisters all the commands
    private void unregisterCommands() {
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        commands.removeIf(command -> {
            knownCommands.remove(command.getName());
            command.getAliases().forEach(knownCommands::remove);

            return true;
        });
    }

}
