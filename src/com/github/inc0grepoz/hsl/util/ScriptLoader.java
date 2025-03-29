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
import org.bukkit.plugin.Plugin;

import com.github.inc0grepoz.hsl.handler.MappedListener;
import com.github.inc0grepoz.hsl.handler.MappedCommand;
import com.github.inc0grepoz.hsl.handler.MethodHandleEventExecutor;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ScriptExecutor;
import com.github.inc0grepoz.ltse.unit.UnitFunction;

@SuppressWarnings("unchecked")
public class ScriptLoader {

    private final ScriptExecutor executor = new ScriptExecutor();
    private final Set<Command> commands = new HashSet<>();
    private final CommandMapProvider commandMap = new CommandMapProvider();
    private final Plugin plugin;

    public ScriptLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initLoaderDirectory()
    {
        File loaderDirectory = new File(plugin.getDataFolder(), "scripts");
        executor.setLoaderDirectory(loaderDirectory);
    }

    public void loadScripts() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection scripts = config.getConfigurationSection("scripts");
        ConfigurationSection commands, events;

        // Temp common
        String fileName;
        Script script;

        for (String scriptName: scripts.getKeys(false)) {
            if (!scripts.getBoolean(scriptName + ".enabled")) {
                continue;
            }

            fileName = scripts.getString(scriptName + ".file");

            try (FileReader reader = new FileReader(new File(plugin.getDataFolder(), fileName))) {
                script = executor.load(reader);
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

    private void loadScriptEvents(String fileName, String scriptName, Script script,
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

    private void registerHandler(Script script, String function, String eventClass,
            EventPriority priority) throws Throwable {

        UnitFunction fn = script.getFunction(function, 1);

        if (fn == null) {
            throw new AssertionError("Missing function " + function);
        }

        Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(eventClass);
        Method method = MappedListener.class.getDeclaredMethods()[0];
        Bukkit.getPluginManager().registerEvent(clazz, new MappedListener<>(fn), priority,
                new MethodHandleEventExecutor(clazz, method), plugin, false);

        plugin.getLogger().info("Registered handler " + function + "(" + eventClass + ")");
    }

    private void loadScriptCommands(String fileName, String scriptName, Script script,
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

    private void registerCommand(Script script, String fnExe, String fnTab,
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

    public void unloadScripts() {
        executor.unloadAll();
        unregisterCommands();
        HandlerList.unregisterAll(plugin);
    }

    private void unregisterCommands() {
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        commands.removeIf(command -> {
            knownCommands.remove(command.getName());
            command.getAliases().forEach(knownCommands::remove);

            return true;
        });
    }

}
