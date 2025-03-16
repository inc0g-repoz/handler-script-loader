package com.github.inc0grepoz.ltse.value;

import java.lang.reflect.Field;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.util.Reflection;

class AccessorField extends AccessorNamed
{

    private Class<?> cachedType;
    private Field cachedField;

    AccessorField(String name)
    {
        super(name);
    }

    @Override
    public String toString()
    {
        return name + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        Class<?> clazz = src.getClass();

        if (cachedType != clazz)
        {
            cachedField = Reflection.findField(cachedType = clazz, name);
        }

        try
        {
            Object rv = access(cachedField, src);
            return elementIndex == null ? rv : accessElement(ctx, rv);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Failed to access the field " + this, t);
        }
    }

    Object access(Field field, Object src) throws Throwable
    {
        boolean accessible = field.isAccessible();
        Object rv = null;

        field.setAccessible(true);
        rv = field.get(src);

        if (!accessible)
        {
            field.setAccessible(accessible);
        }

        return rv;
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        Class<?> clazz = src.getClass();

        if (cachedType != clazz)
        {
            cachedField = Reflection.findField(cachedType = clazz, name);
        }

        if (elementIndex == null)
        {
            if (next == null)
            {
                try
                {
                    return mutate(cachedField, src, convert(val));
                }
                catch (Throwable t)
                {
                    throw new RuntimeException("Failed to access a field " + this, t);
                }
            }
            else
            {
                next.mutate(ctx, access(ctx, src), val);
            }
        }

        try
        {
            return mutateElement(ctx, access(cachedField, src), val);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Failed to mutate an element of field " + this, t);
        }
    }

    Object mutate(Field field, Object src, Object val) throws Throwable
    {
        boolean accessible = field.isAccessible();

        field.setAccessible(true);
        field.set(src, val);

        if (!accessible)
        {
            field.setAccessible(accessible);
        }

        return val;
    }

}
