package com.github.inc0grepoz.ltse.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection
{

    public static Constructor<?> findConstructor(Class<?> clazz, Object[] params, Class<?>[] classes)
    {
        try
        {
            return clazz.getConstructor(classes);
        }
        catch (Throwable t1)
        {
            try
            {
                return clazz.getDeclaredConstructor(classes);
            }
            catch (Throwable t2)
            {}
        }

        for (Constructor<?> next: clazz.getConstructors())
        {
            if (next.getParameterCount() == params.length)
            {
                return next;
            }
        }

        for (Constructor<?> next: clazz.getDeclaredConstructors())
        {
            if (next.getParameterCount() == params.length)
            {
                return next;
            }
        }

        return null;
    }

    public static Method findMethod(Class<?> clazz, String name, Object[] params, Class<?>[] classes)
    {
        try
        {
            return clazz.getMethod(name, classes);
        }
        catch (Throwable t1)
        {
            try
            {
                return clazz.getDeclaredMethod(name, classes);
            }
            catch (Throwable t2)
            {}
        }
    
        for (Method next: clazz.getMethods())
        {
            if (next.getParameterCount() == params.length && next.getName().equals(name))
            {
                return next;
            }
        }
    
        for (Method next: clazz.getDeclaredMethods())
        {
            if (next.getParameterCount() == params.length && next.getName().equals(name))
            {
                return next;
            }
        }
    
        return null;
    }

    public static Field findField(Class<?> clazz, String name)
    {
        try
        {
            return clazz.getField(name);
        }
        catch (Throwable t1)
        {
            try
            {
                return clazz.getDeclaredField(name);
            }
            catch (Throwable t2)
            {
                return null;
            }
        }
    }

}
