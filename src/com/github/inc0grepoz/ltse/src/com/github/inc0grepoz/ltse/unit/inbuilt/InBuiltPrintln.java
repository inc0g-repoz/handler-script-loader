package com.github.inc0grepoz.ltse.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.unit.UnitSection;

public class InBuiltPrintln extends UnitFunction
{

    public InBuiltPrintln(UnitSection parent)
    {
        super(parent, "println", Arrays.asList("array"));
    }

    @Override
    public Object call(Object... params)
    {
        System.out.println(params[0]);
        return params[0];
    }

}
