package com.github.inc0grepoz.ltse.value;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;

/**
 * Represents an accessor for variables stored in a
 * variable pool during execution of some function.
 * 
 * @author inc0g-repoz
 */
public class AccessorVariable extends AccessorNamed
{

    /**
     * Creates and returns a new accessor for a variable
     * with a specified name.
     * 
     * @param name the variable name
     * @return a new {@code AccessorVariable}
     */
    public static AccessorVariable of(String name)
    {
        return new AccessorVariable(name);
    }

   // A package-private constructor
    AccessorVariable(String name)
    {
        super(name);
    }

    @Override
    public String toString()
    {
        return "$" + name + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src) {
        Object rv = ctx.getVariable(name);
        return elementIndex == null ? rv : accessElement(ctx, rv);
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? ctx.setVariable(name, convert(val))
                    : next.mutate(ctx, access(ctx, src), val);
        }

        return mutateElement(ctx, ctx.getVariable(name), val);
    }

}
