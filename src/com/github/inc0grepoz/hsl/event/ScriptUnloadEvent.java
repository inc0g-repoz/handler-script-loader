package com.github.inc0grepoz.hsl.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is called every time before all scripts
 * get unloaded by HandlerScriptLoader. Doesn't hold any
 * useful information and should be used for closing
 * resources, executor services, et cetera, if any opened
 * or created by the scripts.
 * 
 * @author inc0g-repoz
 */
public class ScriptUnloadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
