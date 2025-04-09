package com.github.inc0grepoz.hsl;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.inc0grepoz.hsl.handler.CommandHandler;
import com.github.inc0grepoz.hsl.util.Lix4jLoader;
import com.github.inc0grepoz.hsl.util.ScriptLoader;
import com.github.inc0grepoz.hsl.util.proxy.IScriptExecutor;

/**
 * The main class of HandlerScriptLoader.
 * 
 * @author inc0g-repoz
 */
public class SpigotPlugin extends JavaPlugin {

    private ScriptLoader loader;

    @Override
    public void onEnable() {
        saveDefaults();

        Lix4jLoader lix4jLoader = new Lix4jLoader(this);
        lix4jLoader.update();
        IScriptExecutor executor = lix4jLoader.load();

        loader = new ScriptLoader(this, executor);
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
            saveResource("scripts/example.lix", false);
        }
    }

}
