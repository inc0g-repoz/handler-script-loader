package com.github.inc0grepoz.hsl.handler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class MethodHandleEventExecutor implements EventExecutor {

    private final Class<? extends Event> eventClass;
    private final MethodHandle handle;
    private final Method method;

    public MethodHandleEventExecutor(Class<? extends Event> eventClass, MethodHandle handle) {
        this.eventClass = eventClass;
        this.handle = handle;
        this.method = null;
    }

    public MethodHandleEventExecutor(Class<? extends Event> eventClass, Method method) {
        this.eventClass = eventClass;
        try {
            method.setAccessible(true);
            handle = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to set accessible", e);
        }
        this.method = method;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (!eventClass.isInstance(event)) {
            return;
        }
        try {
            handle.invoke(listener, event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "MethodHandleEventExecutor['" + method + "']";
    }

}
