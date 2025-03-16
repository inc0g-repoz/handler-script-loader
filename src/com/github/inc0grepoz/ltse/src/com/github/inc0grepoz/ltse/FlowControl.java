package com.github.inc0grepoz.ltse;

/**
 * A set of values for controlling execution of scripts.
 * 
 * @author inc0g-repoz
 */
public enum FlowControl
{

    /**
     * Used by script units to continue executing,
     * if one of the units returns a value.
     */
    KEEP_EXECUTING,

    /**
     * Used as a function return value, when no value
     * is returned.
     */
    VOID;

}
