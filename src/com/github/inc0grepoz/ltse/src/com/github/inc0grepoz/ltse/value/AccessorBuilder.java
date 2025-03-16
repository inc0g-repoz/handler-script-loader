package com.github.inc0grepoz.ltse.value;

/**
 * Represents a builder of chained accessors.
 * 
 * @author inc0g-repoz
 */
public class AccessorBuilder
{

    private Accessor first, last;
    private int length;

    // A package-private constructor
    AccessorBuilder() {}

    @Override
    public String toString()
    {
        return first == null ? null : first.toString();
    }

    /**
     * Appends the access chain with a specified {@code Accessor}
     * and returns this builder instance.
     * 
     * @param accessor the next accessor in the chain
     * @return this builder instance
     */
    public AccessorBuilder accessor(Accessor accessor)
    {
        length++;

        if (first == null)
        {
            first = last = accessor;
        }
        else
        {
            last = last.next = accessor;
        }

        return this;
    }

    /**
     * Appends the access chain with a specified class field
     * and returns this builder instance.
     * 
     * @param name the field name
     * @return this builder instance
     */
    public AccessorBuilder field(String name)
    {
        return accessor(new AccessorField(name));
    }

    /**
     * Appends the access chain with a specified class method
     * with precached parameters and returns this builder instance.
     * 
     * @param name   the method name
     * @param params the precached parameters array
     * @return this builder instance
     */
    public AccessorBuilder method(String name, Accessor... params)
    {
        return accessor(new AccessorMethod(name, params));
    }

    /**
     * Appends the access chain with a specified script function
     * with precached parameters and returns this builder instance.
     * 
     * @param name   the function name
     * @param params the precached parameters array
     * @return this builder instance
     */
    public AccessorBuilder function(String name, Accessor... params)
    {
        return accessor(new AccessorFunction(name, params));
    }

    /**
     * Sets the precached element index for the last accessor in
     * the chain and returns this builder instance.
     * 
     * @param accessor the precached element index
     * @return this builder instance
     */
    public AccessorBuilder index(Accessor accessor)
    {
        last.elementIndex = accessor;
        return this;
    }

    /**
     * Returns the head of the accessor-chain.
     * 
     * @return the head of the accessor-chain
     */
    public Accessor build()
    {
        return first;
    }

    /**
     * Returns the accessor-chain length.
     * 
     * @return the accessor-chain length
     */
    public int length()
    {
        return length;
    }

    /**
     * Returns {@code true}, if the accessor-chain contains at least one
     * accessor, or {@code false} otherwise.
     * 
     * @return whether the accessor-chain contains at least one accessor
     */
    public boolean isEmpty()
    {
        return length == 0;
    }

}
