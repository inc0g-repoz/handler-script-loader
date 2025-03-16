package com.github.inc0grepoz.ltse.unit.expression;

import java.util.function.Function;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.value.Accessor;

public class OperatorAssignMutateUnary extends Operator
{

    private final Function<Number, Number> lambda;

    public OperatorAssignMutateUnary(String name, OperatorType type, Function<Number, Number> lambda)
    {
        super(name, type);
        this.lambda = lambda;
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands) {
        if (type == OperatorType.UNARY_LEFT)
        {
            return operands[0].mutate(ctx, null, cv -> lambda.apply((Number) cv));
        }
        else
        {
            Object[] rv_ptr = { null };

            operands[0].mutate(ctx, null, (rv) -> {
                rv_ptr[0] = rv;
                return lambda.apply((Number) rv);
            });

            return rv_ptr[0];
        }
    }

}
