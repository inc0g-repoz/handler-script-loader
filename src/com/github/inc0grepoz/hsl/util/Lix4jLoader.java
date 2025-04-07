package com.github.inc0grepoz.hsl.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.inc0grepoz.hsl.SpigotPlugin;
import com.github.inc0grepoz.hsl.util.proxy.IScriptExecutor;

/**
 * Loads LIX4J into the server memory.
 * 
 * @author inc0g-repoz
 */
public class Lix4jLoader {

    private static final String LIX4J_CLASSPATH = "com.github.inc0grepoz.lix4j.ScriptExecutor";
    private static final String LIX4J_DL_URL = "https://github.com/inc0g-repoz/lix4j/releases/latest/download/lix4j.jar";

    private final SpigotPlugin plugin;
    private final File file;

    /**
     * Creates a new instance of {@code Lix4jLoader} for the specified
     * plugin instance.
     * 
     * @param plugin a plugin instance
     */
    public Lix4jLoader(SpigotPlugin plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "lix4j.jar");
    }

    /**
     * Loads the engine.
     * 
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     */
    public IScriptExecutor load() {
        try {
            URL[] urls = { file.toURI().toURL() };
            URLClassLoader child = new URLClassLoader(urls, getClass().getClassLoader());
            
            // Load the Lix4J class
            Class<?> lix4jClass = Class.forName(LIX4J_CLASSPATH, true, child);
            Object executor = lix4jClass.getConstructor().newInstance();

            // Wrap it in your interface using a Proxy
            return IScriptExecutor.of(executor);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load classes from " + file.getName(), t);
        }
    }

    /**
     * Updates the engine.
     */
    public void update() {
        FileConfiguration config = plugin.getConfig();
        long lastModified;

        if (file.exists()) {
            if (!config.getBoolean("updater.enabled")) {
                return; // no updates requested
            }

            lastModified = lastModified(LIX4J_DL_URL);

            if (lastModified == config.getLong("updater.last-modified-lix4j")) {
                plugin.getLogger().info("LIX4J is up-tp-date");
                return; // no updates released
            }
        } else {
            lastModified = lastModified(LIX4J_DL_URL);
        }

        plugin.getLogger().info("Downloading an updated version of LIX4J...");

        download(LIX4J_DL_URL, file);
        config.set("updater.last-modified-lix4j", lastModified);
        plugin.saveConfig();
        plugin.getLogger().info("LIX4J has been updated successfully");
    }

    // Returns a long value of the last modification
    private long lastModified(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            long dateTime = connection.getLastModified();
            connection.disconnect();
            return dateTime;
        } catch (IOException e) {
            return -1;
        }
    }

    /*
     * Downloads a file by the specified URL {@code String} and writes it's content
     * into the specified file.
     */
    private void download(String downloadURL, File filePath) {
        try (InputStream inputStream = (new URL(downloadURL)).openStream()) {
            Files.copy(inputStream, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
