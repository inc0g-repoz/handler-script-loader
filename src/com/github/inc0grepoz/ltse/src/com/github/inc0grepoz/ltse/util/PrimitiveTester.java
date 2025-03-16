package com.github.inc0grepoz.ltse.util;

/**
 * An utility to test objects and primitives.
 * 
 * @author inc0g-repoz
 */
public class PrimitiveTester
{

    /**
     * Returns {@code true}, if the passed object is a primitive or it's wrapper
     * and {@code false} otherwise.
     * 
     * @param clazz a class to test
     * @return a {@code boolean} value
     */
    public static boolean isPrimitiveType(Object source)
    {
        return isPrimitiveClass(source.getClass());
    }

    /**
     * Returns {@code true}, if the passed {@code Class} represents a primitive
     * or it's wrapper and {@code false} otherwise.
     * 
     * @param clazz a class to test
     * @return a {@code boolean} value
     */
    public static boolean isPrimitiveClass(Class<?> clazz)
    {
        return clazz == Integer.class   || clazz == int.class     ||
               clazz == Byte.class      || clazz == byte.class    ||
               clazz == Character.class || clazz == char.class    ||
               clazz == Boolean.class   || clazz == boolean.class ||
               clazz == Double.class    || clazz == double.class  ||
               clazz == Float.class     || clazz == float.class   ||
               clazz == Long.class      || clazz == long.class    ||
               clazz == Short.class     || clazz == short.class   ||
               clazz == Void.class      || clazz == void.class;
    }

    /**
     * Returns {@code true}, if the passed parameter (can be a primitive or an
     * object as well) has a default value, meaning that one of the following
     * conditions is met:
     * 
     * <li>a passed {@code Object} is {@code null};</li>
     * <li>a passed number is equal to zero or {@code NaN};</li>
     * <li>a passed {@code boolean} value is {@code false}.</li>
     * 
     * <p>If none of the listed conditions are met, returns {@code false}.
     * 
     * @param object a primitive or an object to test
     * @return a {@code boolean} value
     */
    public static boolean isDefaultValue(Object object)
    {
        return object == null ||
               object instanceof Boolean && !((boolean) object) ||
               object instanceof Number  && ((Number) object).doubleValue() == 0d;
    }

    // Instances of this class should never be made
    private PrimitiveTester()
    {
        throw new UnsupportedOperationException();
    }

}
