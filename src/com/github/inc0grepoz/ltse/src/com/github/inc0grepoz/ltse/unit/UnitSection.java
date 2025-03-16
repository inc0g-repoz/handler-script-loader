package com.github.inc0grepoz.ltse.unit;

import java.util.LinkedList;
import java.util.StringJoiner;

import com.github.inc0grepoz.ltse.FlowControl;

public class UnitSection extends Unit
{

    final LinkedList<Unit> childs = new LinkedList<>();

    UnitSection(UnitSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        if (childs.isEmpty())
        {
            return "{}";
        }

        StringJoiner joiner = new StringJoiner("\n", "{\n", "\n}");

        for (Unit child: childs)
        {
            joiner.add(child.toString());
        }

        return joiner.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        Object rv;
        context.enterSection();

        for (Unit unit: childs)
        {
            rv = unit.execute(context);

            if (rv != FlowControl.KEEP_EXECUTING)
            {
                return rv;
            }
        }

        context.exitSection();
        return FlowControl.KEEP_EXECUTING;
    }

}
