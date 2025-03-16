package com.github.inc0grepoz.ltse.value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.util.Reflection;

class AccessorMethod extends AccessorNamed
{

    private final Accessor[] params;

    private Class<?> cachedType;
    private Method cachedMethod;

    AccessorMethod(String name, Accessor[] params)
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
        Object[] paramArr = cacheMethod(ctx, src);

        try
        {
            Object rv = access(cachedMethod, src, paramArr);
            return elementIndex == null ? rv : accessElement(ctx, rv);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Failed to invoke the method " + this, t);
        }
    }

    Object access(Method method, Object src, Object[] params)
    throws Throwable
    {
        boolean accessible = method.isAccessible();
        Object rv = null;

        method.setAccessible(true);
        rv = method.invoke(src, params);

        if (!accessible)
        {
            method.setAccessible(accessible);
        }

        return rv;
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? super.mutate(ctx, src, val)
                    : next.mutate(ctx, access(ctx, src), val);
        }

        Object[] paramArr = cacheMethod(ctx, src);

        try
        {
            return mutateElement(ctx, access(cachedMethod, src, paramArr), val);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Failed to mutate a return value element of method " + this, t);
        }
    }

    private Object[] cacheMethod(ExecutionContext ctx, Object src)
    {
        Object[]   paramArr;
        Class<?>[] classArr;

        if (params == null || params.length == 0)
        {
            paramArr = new Object[0];
            classArr = new Class<?>[0];
        }
        else
        {
            List<Object>   paramList = new ArrayList<>();
            List<Class<?>> classList = new ArrayList<>();
            Object puw;

            for (int i = 0; i < params.length; i++)
            {
                if (params[i] == null)
                {
                    continue;
                }

                paramList.add(puw = params[i].linkedAccess(ctx, null));
                classList.add(puw.getClass());
            }

            paramList.toArray(paramArr = new Object[paramList.size()]);
            classList.toArray(classArr = new Class<?>[classList.size()]);
        }

        Class<?> clazz = src.getClass();

        if (cachedType != clazz)
        {
            cachedMethod = Reflection.findMethod(cachedType = clazz, name, paramArr, classArr);
        }

        return paramArr;
    }

}
