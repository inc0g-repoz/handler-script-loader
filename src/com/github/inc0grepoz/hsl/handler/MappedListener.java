package com.github.inc0grepoz.hsl.handler;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.inc0grepoz.ltse.unit.UnitFunction;

/**
 * A single event listener handled by a function.
 * 
 * @author inc0g-repoz
 * @param <T> the event type
 */
public class MappedListener<T extends Event> implements Listener {

    private final UnitFunction fn;

    public MappedListener(UnitFunction fn) {
        this.fn = fn;
    }

    @EventHandler
    public void onEvent(T event) {
        fn.call(event);
    }

}
