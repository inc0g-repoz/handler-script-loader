package com.github.inc0grepoz.ltse.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.github.inc0grepoz.ltse.util.AnsiColor;

public class ASTNodeSection extends ASTNode
{

    protected final LinkedList<ASTNode> childs = new LinkedList<>();
    protected final AnsiColor color = AnsiColor.randomDarkBright();

    ASTNodeSection(ASTNodeSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        String childs = this.childs.stream().map(ASTNode::toString).collect(Collectors.joining("\n"));
        return color + childs + (parent == null ? AnsiColor.RESET : parent.color);
    }

    @Override
    public LinkedList<ASTNode> getChildNodes()
    {
        return childs;
    }

    @Override
    public boolean hasChildNodes()
    {
        return true;
    }

    @Override
    public boolean hasTokens()
    {
        return false;
    }

}
