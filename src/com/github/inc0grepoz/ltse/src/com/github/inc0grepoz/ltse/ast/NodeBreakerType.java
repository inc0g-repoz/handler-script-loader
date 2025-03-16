package com.github.inc0grepoz.ltse.ast;

/**
 * Represents a node breaker type used bye token containers for
 * distinguishing blocks from statements and vice versa.
 * 
 * @author inc0g-repoz
 */
public enum NodeBreakerType
{

    /** Indicates that some token container is followed by block. */
    BLOCK,

    /** Indicates that some token container is followed by statement. */
    STATEMENT;

}
