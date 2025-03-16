package com.github.inc0grepoz.ltse;

import java.util.ArrayList;
import java.util.List;

import com.github.inc0grepoz.ltse.ast.AST;
import com.github.inc0grepoz.ltse.unit.ExecutionContext;
import com.github.inc0grepoz.ltse.unit.ScriptCompiler;
import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.unit.UnitRoot;
import com.github.inc0grepoz.ltse.unit.expression.Operator;

/**
 * Represents a compiled script.
 * 
 * @author inc0g-repoz
 */
public class Script
{

    private final List<Operator> operators = new ArrayList<>();
    private final ExecutionContext globalContext = new ExecutionContext(this);
    private final UnitRoot root;

    // A package-private constructor
    Script(ScriptExecutor executor, AST ast)
    {
        executor.getDefaultOperators().forEach(operators::add);
        root = ScriptCompiler.compile(this, ast, executor::supplyInbuiltFunctions);

        globalContext.enterSection();
        root.init(globalContext);
    }

    /**
     * Returns the function by the specified name and parameters
     * count, if exists, or {@code null} otherwise.
     * 
     * @param name       the function name
     * @param paramCount the parameters count
     * @return a function
     */
    public UnitFunction getFunction(String name, int paramCount)
    {
        return root.getFunction(name, paramCount);
    }

    /**
     * Calls the function with the specified name and parameters
     * count, if exists.
     * 
     * @param name   the function name
     * @param params the parameters
     * @return the function return value
     * @throws IllegalArgumentException
     *         if no function has the same name and parameters count
     */
    public Object callFunction(String name, Object... params)
    {
        return root.getFunction(name, params.length).call(params);
    }

    /**
     * Returns the list of operators used by this {@code Script}.
     * 
     * @return the list of operators
     */
    public List<Operator> getOperators()
    {
        return operators;
    }

    /**
     * Supplies the execution context copy to a function.
     * 
     * @return a clone of the script execution context
     */
    public ExecutionContext supplyContext()
    {
        return globalContext.clone();
    }

}
