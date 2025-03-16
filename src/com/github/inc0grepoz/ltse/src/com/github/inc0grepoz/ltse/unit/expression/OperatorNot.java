package com.github.inc0grepoz.ltse.unit.expression;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.util.PrimitiveTester;
import com.github.inc0grepoz.ltse.value.Accessor;

public class OperatorNot extends Operator
{

    public OperatorNot(String name)
    {
        super(name, OperatorType.UNARY_LEFT);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands) {
        return PrimitiveTester.isDefaultValue(operands[0].access(ctx, null));
    }

}
