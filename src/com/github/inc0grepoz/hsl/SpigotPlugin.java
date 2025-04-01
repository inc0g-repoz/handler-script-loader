package com.github.inc0grepoz.hsl;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.inc0grepoz.hsl.handler.CommandHandler;
import com.github.inc0grepoz.hsl.util.ScriptLoader;

/**
 * The main class of HandlerScriptLoader.
 * 
 * @author inc0g-repoz
 */
public class SpigotPlugin extends JavaPlugin {

    private final ScriptLoader loader = new ScriptLoader(this);

    @Override
    public void onEnable() {
        saveDefaults();

        loader.initLoaderDirectory();
        loader.loadScripts();

        String command = getDescription().getCommands().keySet().iterator().next();
        getCommand(command).setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        loader.unloadScripts();
    }

    /**
     * Reloads the plugin configuration and scripts.
     */
    public void reload() {
        loader.unloadScripts();
        reloadConfig();
        loader.loadScripts();
    }

    /**
     * Saves default resources.
     */
    public void saveDefaults() {
        if (!getDataFolder().exists()) {
            saveDefaultConfig();
            saveResource("scripts/example.script", false);
        }
    }

}
