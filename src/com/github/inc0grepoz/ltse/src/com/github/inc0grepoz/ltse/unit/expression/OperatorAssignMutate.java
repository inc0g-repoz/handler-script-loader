package com.github.inc0grepoz.ltse.unit.expression;

import java.util.function.BiFunction;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.value.Accessor;

public class OperatorAssignMutate extends Operator
{

    private final BiFunction<Number, Number, Number> lambda;

    public OperatorAssignMutate(String name, BiFunction<Number, Number, Number> lambda)
    {
        super(name, OperatorType.BINARY);
        this.lambda = lambda;
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands) {
        if (operands.length != type.getOperandCount())
        {
            throw new RuntimeException("Only can use \"" + name + "\" on " + type.getOperandCount() + " numbers");
        }

        Number rv = (Number) operands[1].linkedAccess(ctx, null);

        return operands[0].mutate(ctx, null, cv -> lambda.apply((Number) cv, rv));
    }

}
