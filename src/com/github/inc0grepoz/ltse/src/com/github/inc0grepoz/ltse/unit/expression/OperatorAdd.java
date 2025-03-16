package com.github.inc0grepoz.ltse.unit.expression;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.value.Accessor;

public class OperatorAdd extends Operator
{

    public OperatorAdd(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands) {
        if (operands[0] == Accessor.NULL)
        {
            operands[0] = Accessor.ZERO;
        }

        Object[] objects = new Object[operands.length];
        boolean allNumbers = true;

        for (int i = 0; i < operands.length; i++)
        {
            objects[i] = operands[i].linkedAccess(ctx, null);

            if (!(objects[i] instanceof Number))
            {
                allNumbers = false;
            }
        }

        if (allNumbers)
        {
            double rv = 0d;

            for (int i = 0; i < objects.length; i++)
            {
                rv += ((Number) objects[i]).doubleValue();
            }

            return rv;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < objects.length; i++)
        {
            builder.append(objects[i]);
        }

        return builder.toString();
    }

}
