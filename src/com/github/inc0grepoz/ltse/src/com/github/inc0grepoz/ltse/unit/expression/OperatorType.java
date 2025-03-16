package com.github.inc0grepoz.ltse.unit.expression;

public enum OperatorType
{

    UNARY_LEFT(1),
    UNARY_RIGHT(1),
    BINARY(2),
    TERNARY(3);

    private final int operandCount;

    OperatorType(int operandCount)
    {
        this.operandCount = operandCount;
    }

    public int getOperandCount()
    {
        return operandCount;
    }

}
