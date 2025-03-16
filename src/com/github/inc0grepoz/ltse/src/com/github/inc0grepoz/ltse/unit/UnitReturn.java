package com.github.inc0grepoz.ltse.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ast.ASTNode;
import com.github.inc0grepoz.ltse.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.ltse.value.Accessor;

public class UnitReturn extends Unit
{

    static UnitReturn compile(Script script, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // return

        Accessor rva = tokens.isEmpty()
                ? Accessor.VOID
                : ExpressionResolver.resolve(script, tokens);

        return new UnitReturn(parent, rva);
    }

    private final Accessor accessor;

    UnitReturn(UnitSection parent, Accessor accessor)
    {
        super(parent);
        this.accessor = accessor;
    }

    @Override
    public String toString()
    {
        return "return " + accessor;
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return accessor.linkedAccess(context, null);
    }

}
