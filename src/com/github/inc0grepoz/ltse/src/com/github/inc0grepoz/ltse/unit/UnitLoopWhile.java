package com.github.inc0grepoz.ltse.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.ltse.FlowControl;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ast.ASTNode;
import com.github.inc0grepoz.ltse.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.ltse.util.PrimitiveTester;
import com.github.inc0grepoz.ltse.util.TokenHelper;
import com.github.inc0grepoz.ltse.value.Accessor;

public class UnitLoopWhile extends UnitSection
{

    static UnitLoopWhile compile(Script script, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // while

        LinkedList<String> conditionTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        Accessor condition = ExpressionResolver.resolve(script, conditionTokens);

        UnitLoopWhile unit = new UnitLoopWhile(section, condition);
        ScriptCompiler.appendSectionUnits(script, node, unit);

        return unit;
    }

    final Accessor condition;

    UnitLoopWhile(UnitSection parent, Accessor condition)
    {
        super(parent);
        this.condition = condition;
    }

    @Override
    public String toString()
    {
        return "while (" + condition + ") " + super.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        while (!PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null)))
        {
            Object rv = super.execute(context);

            if (rv != FlowControl.KEEP_EXECUTING)
            {
                return rv;
            }
        }

        return FlowControl.KEEP_EXECUTING;
    }

}
