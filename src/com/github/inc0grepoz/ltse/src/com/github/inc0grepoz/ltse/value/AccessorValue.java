package com.github.inc0grepoz.ltse.value;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;

/**
 * Stores a single precached Java object or a wrapped
 * primitive value for a read-only access.
 * 
 * @author inc0g-repoz
 */
public class AccessorValue extends Accessor
{

    /**
     * Creates a new {@code AccessorValue} with a specified
     * instance stored.
     * 
     * @param instance an {@code Object} to store
     * @return a new {@code AccessorValue}
     */
    public static AccessorValue of(Object instance)
    {
        return new AccessorValue(instance);
    }

    final Object instance;

    AccessorValue(Object instance)
    {
        this.instance = instance;
    }

    @Override
    public String toString()
    {
        if (instance == null)
        {
            return null;
        }

        String rv = instance.toString();

        if (instance instanceof String)
        {
            rv = '"' + rv + '"';
        }

        return rv + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        return instance;
    }

}
