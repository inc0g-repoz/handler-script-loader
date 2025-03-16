package com.github.inc0grepoz.ltse.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.ltse.FlowControl;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ast.ASTNode;
import com.github.inc0grepoz.ltse.exception.SyntaxError;
import com.github.inc0grepoz.ltse.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.ltse.util.PrimitiveTester;
import com.github.inc0grepoz.ltse.util.TokenHelper;
import com.github.inc0grepoz.ltse.value.Accessor;

public class UnitLoopFor extends UnitSection
{

    static UnitLoopFor compile(Script script, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // for

        LinkedList<String> tempTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        TokenHelper.openParentheses(tempTokens);
        LinkedList<LinkedList<String>> split = TokenHelper.splitTokens(tempTokens, ";");

        if (split.size() != 3)
        {
            throw new SyntaxError("Bad for-loop syntax");
        }

        Accessor parameter = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(script, tempTokens);
        Accessor condition = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(script, tempTokens);
        Accessor increment = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(script, tempTokens);

        UnitLoopFor unit = new UnitLoopFor(section, parameter, condition, increment);
        ScriptCompiler.appendSectionUnits(script, node, unit);

        return unit;
    }

    final Accessor parameter, condition, increment;

    UnitLoopFor(UnitSection parent, Accessor parameter, Accessor condition, Accessor increment)
    {
        super(parent);
        this.parameter = parameter;
        this.condition = condition;
        this.increment = increment;
    }

    @Override
    public String toString()
    {
        return "for (" + parameter + "; " + condition + "; " + increment + ") " + super.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        if (parameter != null)
        {
            parameter.linkedAccess(context, null);
        }

        while (condition == null || !PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null)))
        {
            Object rv = super.execute(context);

            if (rv != FlowControl.KEEP_EXECUTING)
            {
                return rv;
            }

            if (increment != null)
            {
                increment.access(context, null);
            }
        }

        return FlowControl.KEEP_EXECUTING;
    }

}
