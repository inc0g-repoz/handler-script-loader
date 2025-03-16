package com.github.inc0grepoz.ltse.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.unit.UnitSection;

public class InBuiltPrint extends UnitFunction
{

    public InBuiltPrint(UnitSection parent)
    {
        super(parent, "print", Arrays.asList("array"));
    }

    @Override
    public Object call(Object... params)
    {
        System.out.print(params[0]);
        return params[0];
    }

}
