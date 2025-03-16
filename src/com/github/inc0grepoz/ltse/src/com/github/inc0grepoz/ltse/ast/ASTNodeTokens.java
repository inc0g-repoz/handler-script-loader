package com.github.inc0grepoz.ltse.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ASTNodeTokens extends ASTNode
{

    protected final LinkedList<String> tokens = new LinkedList<>();

    protected NodeBreakerType breaker = NodeBreakerType.STATEMENT;

    ASTNodeTokens(ASTNodeSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        String indentation = new String(new char[indentation()]).replace("\0", "    ");
        return indentation + tokens.stream().collect(Collectors.joining(" ")) + " " + breaker;
    }

    @Override
    public LinkedList<String> getTokens()
    {
        return tokens;
    }

    @Override
    public NodeBreakerType getNodeBreakerType()
    {
        return breaker;
    }

    public void setNodeBreakerType(NodeBreakerType breaker)
    {
        this.breaker = breaker;
    }

    @Override
    public boolean hasChildNodes()
    {
        return false;
    }

    @Override
    public boolean hasTokens()
    {
        return true;
    }

}
