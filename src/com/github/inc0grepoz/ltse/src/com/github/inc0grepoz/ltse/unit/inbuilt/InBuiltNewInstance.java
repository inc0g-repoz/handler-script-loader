package com.github.inc0grepoz.ltse.unit.inbuilt;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.unit.UnitSection;
import com.github.inc0grepoz.ltse.util.Reflection;

public class InBuiltNewInstance extends UnitFunction
{

    public InBuiltNewInstance(UnitSection parent)
    {
        super(parent, "new_instance", Arrays.asList("class", "params"));
    }

    @Override
    public Object call(Object... params)
    {
        try {
            Class<?> clazz = Class.forName((String) params[0]);

            Object  [] paramArray = (Object[]) params[1];
            Class<?>[] clazzArray = new Class[paramArray.length];

            for (int i = 0; i < paramArray.length; i++)
            {
                clazzArray[i] = paramArray[i].getClass();
            }

            Constructor<?> constructor = Reflection.findConstructor(clazz, paramArray, clazzArray);
            return constructor.newInstance(paramArray);
        } catch (Throwable t) {
            throw new IllegalArgumentException("Failed to create an instance of " + params[0], t);
        }
    }

}
