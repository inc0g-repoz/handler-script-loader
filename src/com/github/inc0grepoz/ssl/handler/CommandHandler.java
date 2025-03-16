package com.github.inc0grepoz.ssl.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.inc0grepoz.ssl.SpigotPlugin;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final SpigotPlugin plugin;

    public CommandHandler(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        if (args.length != 1) {
            return false;
        }

        switch (args[0]) {
        case "reload":
            plugin.reload();
            sender.sendMessage("Reloaded scripts");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
            String label, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("reload");
        }

        return Collections.emptyList();
    }

}
