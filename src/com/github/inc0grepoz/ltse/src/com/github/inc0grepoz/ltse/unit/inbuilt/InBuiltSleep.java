package com.github.inc0grepoz.ltse.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.ltse.FlowControl;
import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.unit.UnitSection;

public class InBuiltSleep extends UnitFunction
{

    public InBuiltSleep(UnitSection parent)
    {
        super(parent, "sleep", Arrays.asList("time"));
    }

    @Override
    public Object call(Object... params)
    {
        try
        {
            Thread.sleep(((Number) params[0]).longValue());
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        return FlowControl.VOID;
    }

}
