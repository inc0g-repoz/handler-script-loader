package com.github.inc0grepoz.hsl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

public class CommandMapProvider {

    private CommandMap commandMap;
    private Map<String, Command> knownCommands;

    public CommandMap get() {
        if (commandMap != null) {
            return commandMap;
        }

        Server server = Bukkit.getServer();

        try {
            Field field = server.getClass().getField("commandMap");

            boolean accessible = field.isAccessible();
            field.setAccessible(true);

            commandMap = (CommandMap) field.get(server);

            field.setAccessible(accessible);
        } catch (Throwable t1) {
            try {
                Method method = server.getClass().getMethod("getCommandMap");

                boolean accessible = method.isAccessible();
                method.setAccessible(true);

                commandMap = (CommandMap) method.invoke(server);

                method.setAccessible(accessible);
            } catch (Throwable t2) {
                throw new RuntimeException("Could not access the command map");
            }
        }

        return commandMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Command> getKnownCommands() {
        if (knownCommands != null) {
            return knownCommands;
        }

        CommandMap map = get();

        try {
            Field field = map.getClass().getField("knownCommands");

            boolean accessible = field.isAccessible();
            field.setAccessible(true);

            knownCommands = (Map<String, Command>) field.get(map);

            field.setAccessible(accessible);
        } catch (Throwable t1) {
            try {
                Method method = map.getClass().getMethod("getKnownCommands");

                boolean accessible = method.isAccessible();
                method.setAccessible(true);

                knownCommands = (Map<String, Command>) method.invoke(map);

                method.setAccessible(accessible);
            } catch (Throwable t2) {
                throw new RuntimeException("Could not access the command map");
            }
        }

        return knownCommands;
    }

}
