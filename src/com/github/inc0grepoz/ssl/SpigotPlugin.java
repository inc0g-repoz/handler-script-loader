package com.github.inc0grepoz.ssl;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.inc0grepoz.ssl.handler.CommandHandler;
import com.github.inc0grepoz.ssl.util.ScriptLoader;

public class SpigotPlugin extends JavaPlugin {

    private final ScriptLoader loader = new ScriptLoader(this);

    @Override
    public void onEnable() {
        saveDefaults();

        loader.loadScripts();

        getCommand("spigot-script-loader").setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        loader.unloadScripts();
        HandlerList.unregisterAll(this);
    }

    public void reload() {
        loader.unloadScripts();
        reloadConfig();
        loader.loadScripts();
    }

    public void saveDefaults() {
        if (!getDataFolder().exists()) {
            saveDefaultConfig();
            saveResource("scripts/example.script", false);
        }
    }

}
