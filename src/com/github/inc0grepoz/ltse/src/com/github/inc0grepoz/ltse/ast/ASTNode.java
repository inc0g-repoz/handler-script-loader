package com.github.inc0grepoz.ltse.ast;

import java.util.LinkedList;

/**
 * Represents a node in an abstract syntax tree.
 * May contain child nodes or tokens.
 * 
 * @author inc0g-repoz
 */
public abstract class ASTNode
{

    final ASTNodeSection parent;

    // Creates an abstract ASTNode
    ASTNode(ASTNodeSection parent)
    {
        this.parent = parent;

        if (parent != null)
        {
            parent.childs.add(this);
        }
    }

    /**
     * Returns the parent {@code ASTNodeSection},
     * if exists, or {@code null} otherwise.
     * 
     * @return the parent node section
     */
    public ASTNodeSection getParent()
    {
        return parent;
    }

    /**
     * Returns the linked list of child nodes, if
     * the node implementation stores them.
     * 
     * @return a linked list of child nodes
     * @throws UnsupportedOperationException
     *         if the node implementation can only
     *         store tokens
     */
    public LinkedList<ASTNode> getChildNodes()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the linked list of stored tokens, if
     * the node implementation stores them.
     * 
     * @return a linked list of tokens
     * @throws UnsupportedOperationException
     *         if the node implementation can only
     *         store child nodes
     */
    public LinkedList<String> getTokens()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the breaker type before the next
     * node in the section, if the implementation
     * stores child nodes.
     * 
     * @return a {@code NodeBreakerType} value
     * @throws UnsupportedOperationException
     *         if the node implementation can only
     *         store tokens
     */
    public NodeBreakerType getNodeBreakerType()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true}, if the node implementation
     * can store child nodes, or {@code false} otherwise.
     * 
     * @return whether the node implementation can store
     *         child nodes
     */
    public abstract boolean hasChildNodes();

    /**
     * Returns {@code true}, if the node implementation
     * can store tokens, or {@code false} otherwise.
     * 
     * @return whether the node implementation can store
     *         tokens
     */
    public abstract boolean hasTokens();

    // Used for printing the AST
    int indentation()
    {
        ASTNode node = parent;

        int i = 0;
        for (; node != null; i++)
        {
            node = node.parent;
        }

        return i;
    }

}
