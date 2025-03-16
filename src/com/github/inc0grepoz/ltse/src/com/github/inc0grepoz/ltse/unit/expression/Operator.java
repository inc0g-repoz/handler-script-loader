package com.github.inc0grepoz.ltse.unit.expression;

import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.value.Accessor;

/**
 * Represents an abstract implementation of some operator.
 * 
 * @author inc0g-repoz
 */
public abstract class Operator
{

    final String name;
    final OperatorType type;

    /**
     * Creates a new {@code operator} abstraction with a
     * name and type specified.
     * 
     * @param name
     * @param type
     */
    protected Operator(String name, OperatorType type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of this {@code Operator}
     * 
     * @return the name of this {@code Operator}
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the type of this {@code Operator}
     * 
     * @return the type of this {@code Operator}
     */
    public OperatorType getType()
    {
        return type;
    }

    /**
     * Calculates and returns the result of the operation
     * performed on the specified operands in the given
     * function {@code ExecutionContext}.
     * 
     * @param ctx      a function {@code ExecutionContext}
     * @param operands an array of accessors for operands
     * @return the result of the operation performed
     */
    public abstract Object evaluate(ExecutionContext ctx, Accessor[] operands);

}
