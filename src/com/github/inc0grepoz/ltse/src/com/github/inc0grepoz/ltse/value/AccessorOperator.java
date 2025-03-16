package com.github.inc0grepoz.ltse.value;

import java.util.StringJoiner;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.unit.expression.Operator;

/**
 * Represents an {@code Accessor} for values processed by operators
 * used in the execution flow of some {@code Script}.
 * 
 * @author inc0g-repoz
 */
public class AccessorOperator extends Accessor
{

    /**
     * Creates and returns a new {@code AccessorOperator} from the specified
     * operator implementation and precached operands accessors.
     * 
     * @param operator the operator implementation
     * @param operands the operands accessors
     * @return a new {@code AccessorOperator}
     */
    public static AccessorOperator of(Operator operator, Accessor... operands)
    {
        return new AccessorOperator(operator, operands);
    }

    private final Operator operator;
    private final Accessor[] operands;

    // A package-private constructor
    AccessorOperator(Operator operator, Accessor[] operands)
    {
        this.operator = operator;
        this.operands = operands;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" " + operator.getName() + " ");

        for (int i = 0; i < operands.length; i++)
        {
            joiner.add(operands[i].toString());
        }

        return "(" + joiner.toString() + ")" + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        return operator.evaluate(ctx, operands);
    }

}
