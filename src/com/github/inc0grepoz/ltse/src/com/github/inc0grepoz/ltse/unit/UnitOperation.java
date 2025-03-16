package com.github.inc0grepoz.ltse.unit;

import com.github.inc0grepoz.ltse.FlowControl;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ast.ASTNode;
import com.github.inc0grepoz.ltse.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.ltse.value.Accessor;

public class UnitOperation extends Unit
{

    static UnitOperation compile(Script script, ASTNode node, UnitSection parent)
    {
        return new UnitOperation(parent, ExpressionResolver.resolve(script, node.getTokens()));
    }

    private Accessor accessor;

    UnitOperation(UnitSection parent, Accessor accessor) {
        super(parent);
        this.accessor = accessor;
    }

    @Override
    public String toString()
    {
        return accessor.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        accessor.linkedAccess(context, null);
        return FlowControl.KEEP_EXECUTING;
    }

}
