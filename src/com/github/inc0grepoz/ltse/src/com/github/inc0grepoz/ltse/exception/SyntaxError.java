package com.github.inc0grepoz.ltse.exception;

/**
 * An exception that can be thrown due to bad syntax
 * during a script compilation.
 * 
 * @author inc0g-repoz
 */
public final class SyntaxError extends RuntimeException
{

    // Implements serializable
    private static final long serialVersionUID = -7932907360413850284L;

    /**
     * Constructs a new syntax error with the specified detail message.
     * 
     * @param message the detail message
     */
    public SyntaxError(String message)
    {
        super(message);
    }

    /**
     * Constructs a new syntax error with the specified detail message
     * and cause.
     * 
     * @param message   the detail message
     * @param throwable the cause
     */
    public SyntaxError(String message, Throwable throwable)
    {
        super(message, throwable);
    }

}
