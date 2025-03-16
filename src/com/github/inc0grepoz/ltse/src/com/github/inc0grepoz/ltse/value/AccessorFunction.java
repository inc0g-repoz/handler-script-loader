package com.github.inc0grepoz.ltse.value;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.unit.UnitFunction;

class AccessorFunction extends AccessorNamed
{

    private final Accessor[] params;

    private UnitFunction cachedFunction;

    AccessorFunction(String name, Accessor[] params)
    {
        super(name);
        this.params = params;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");

        for (int i = 0; i < params.length; i++)
        {
            joiner.add(params[i].toString());
        }

        return name + joiner.toString() + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        Object[] paramArr;

        if (params == null || params.length == 0)
        {
            paramArr = new Object[0];
        }
        else
        {
            List<Object> paramList = new ArrayList<>();

            for (int i = 0; i < params.length; i++)
            {
                if (params[i] == null)
                {
                    continue;
                }

                paramList.add(params[i].linkedAccess(ctx, null));
            }

            paramList.toArray(paramArr = new Object[paramList.size()]);
        }

        if (cachedFunction == null)
        {
            cachedFunction = ctx.getScript().getFunction(name, paramArr.length);
        }

        try
        {
            return cachedFunction.call(paramArr);
        }
        catch (NullPointerException npe)
        {
            throw new RuntimeException("Unknown function " + name + " with " + params.length + " parameter(-s)", npe);
        }
        catch (Throwable t)
        {
            throw t;
        }
    }

}
