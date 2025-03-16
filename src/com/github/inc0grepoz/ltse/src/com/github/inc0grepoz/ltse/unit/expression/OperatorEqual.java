package com.github.inc0grepoz.ltse.unit.expression;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.util.PrimitiveTester;
import com.github.inc0grepoz.ltse.value.Accessor;

public class OperatorEqual extends Operator
{

    public OperatorEqual(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        Object scd, fst = operands[0].linkedAccess(ctx, null);
        boolean equal = true;

        for (int i = 1; i < operands.length; i++)
        {
            scd = operands[i].linkedAccess(ctx, null);

            if (PrimitiveTester.isPrimitiveType(fst)
                    && PrimitiveTester.isPrimitiveType(scd))
            {
                if (fst instanceof Number && scd instanceof Number)
                {
                    equal = ((Number) fst).doubleValue() == ((Number) scd).doubleValue();
                }
                else
                {
                    equal = fst.equals(scd);
                }
            }
            else
            {
                equal = fst == scd;
            }

            if (!equal)
            {
                return false;
            }

            fst = scd;
        }

        return true;
    }

}
