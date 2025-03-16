package com.github.inc0grepoz.ltse.unit.expression;

import java.util.LinkedList;
import java.util.regex.Pattern;

import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.exception.SyntaxError;
import com.github.inc0grepoz.ltse.util.TokenHelper;
import com.github.inc0grepoz.ltse.value.Accessor;
import com.github.inc0grepoz.ltse.value.AccessorBuilder;
import com.github.inc0grepoz.ltse.value.AccessorOperator;
import com.github.inc0grepoz.ltse.value.AccessorValue;
import com.github.inc0grepoz.ltse.value.AccessorVariable;

public class ExpressionResolver
{

    static final Pattern PATTERN_STRING = Pattern.compile("^\".+\"$");
    static final Pattern PATTERN_SPECIAL = Pattern.compile("[~!@#$%^&*()-=+\\[\\]{}:;'\"\\|/<>,.?]+");
    static final Pattern PATTERN_NUMBER_INT = Pattern.compile("\\d+");
    static final Pattern PATTERN_NUMBER_LONG = Pattern.compile("\\d+[Ll]");
    static final Pattern PATTERN_NUMBER_FLOAT = Pattern.compile("\\d*\\.\\d+[Ff]");
    static final Pattern PATTERN_NUMBER_DOUBLE = Pattern.compile("\\d*\\.\\d+[Dd]?");

    public static Accessor resolve(Script script, LinkedList<String> tokens)
    {
        TokenHelper.openParentheses(tokens);

        if (tokens.size() == 1)
        {
            return resolveToken(tokens.getFirst());
        }

//      throw new RuntimeException("Unresolved expression: " + String.join(" ", tokens));
        return resolveOperator(script, tokens);
    }

    private static Accessor resolveToken(String token)
    {
        if (token == null)
        {
            return Accessor.NULL;
        }

        // Persistent (reserved tokens)
        switch (token)
        {
        case "null":
            return Accessor.NULL;
        case "true":
            return Accessor.TRUE;
        case "false":
            return Accessor.FALSE;
        }

        // String tokens
        if (token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"')
        {
            return AccessorValue.of(token.substring(1, token.length() - 1));
        }

        // Integer tokens
        if (PATTERN_NUMBER_INT.matcher(token).matches())
        {
            return AccessorValue.of(Integer.parseInt(token));
        }

        // Long tokens
        if (PATTERN_NUMBER_LONG.matcher(token).matches())
        {
            return AccessorValue.of(Long.parseLong(token));
        }

        // Float tokens
        if (PATTERN_NUMBER_FLOAT.matcher(token).matches())
        {
            return AccessorValue.of(Float.parseFloat(token));
        }

        // Double tokens
        if (PATTERN_NUMBER_DOUBLE.matcher(token).matches())
        {
            return AccessorValue.of(Double.parseDouble(token));
        }

        // Variable
        return AccessorVariable.of(token);
    }

    private static Accessor resolveOperator(Script script, LinkedList<String> tokens)
    {
        LinkedList<LinkedList<String>> separateTokens;

        for (Operator operator: script.getOperators())
        {
            switch (operator.getType())
            {
            case UNARY_LEFT:
                if (!operator.getName().equals(tokens.peekFirst()))
                {
                    break;
                }

                tokens.pollFirst();
                return AccessorOperator.of(operator, resolve(script, tokens));
            case UNARY_RIGHT:
                if (!operator.getName().equals(tokens.peekLast()))
                {
                    break;
                }

                tokens.pollLast();
                return AccessorOperator.of(operator, resolve(script, tokens));
            case BINARY:
                separateTokens = TokenHelper.splitTokens(tokens, operator.getName());

                if (separateTokens.size() > 1)
                {
                    Accessor[] operands = new Accessor[separateTokens.size()];

                    for (int i = 0; i < operands.length; i++)
                    {
                        operands[i] = resolve(script, separateTokens.poll());
                    }

                    return AccessorOperator.of(operator, operands);
                }

                break;
            case TERNARY:
                // Unresolved
            }
        }

        return resolveLinkedAccessor(script, tokens);
    }

    private static Accessor resolveLinkedAccessor(Script script, LinkedList<String> tokens)
    {
        LinkedList<LinkedList<String>> splitTokens = TokenHelper.splitTokens(tokens, ".");
        LinkedList<String> nextTokenList;
        AccessorBuilder builder = Accessor.builder();
        Accessor index = null;

        while (!splitTokens.isEmpty())
        {
            nextTokenList = splitTokens.poll();

            if (("]").equals(nextTokenList.peekLast()))
            {
                index = resolve(script, TokenHelper.readEnclosedTokensBackwards(nextTokenList, "[", "]"));
            }
            else
            {
                index = null;
            }

            if (("(").equals(nextTokenList.peekFirst())) // value
            {
                if (builder.isEmpty())
                {
                    builder.accessor(resolve(script, nextTokenList));
                    builder.index(index);
                }
                else
                {
                    throw new SyntaxError("Illegal member name: " + String.join(" ", nextTokenList));
                }
            }
            else if ((")").equals(nextTokenList.peekLast())) // parameterized accessor
            {
                String name = nextTokenList.poll();
                TokenHelper.openParentheses(nextTokenList);

                Accessor[] accessors;
                if (nextTokenList.isEmpty())
                {
                    accessors = new Accessor[0];
                }
                else
                {
                    LinkedList<LinkedList<String>> paramList = TokenHelper.splitTokens(nextTokenList, ",");
                    accessors = new Accessor[paramList.size()];

                    for (int i = 0; i < accessors.length; i++)
                    {
                        accessors[i] = resolve(script, paramList.poll());
                    }
                }

                if (builder.isEmpty()) // in the beginning
                {
                    builder.function(name, accessors);
                    builder.index(index);
                }
                else // somewhere later
                {
                    builder.method(name, accessors);
                    builder.index(index);
                }
            }
            else // field
            {
                if (nextTokenList.size() != 1)
                {
                    throw new SyntaxError("Unresolved expression " + String.join(" ", nextTokenList));
                }

                if (builder.isEmpty())
                {
                    builder.accessor(resolveToken(nextTokenList.poll()));
                    builder.index(index);
                }
                else
                {
                    builder.field(nextTokenList.poll());
                    builder.index(index);
                }
            }
        }

//      System.out.println("Linked Accessor: " + builder);
//      System.out.println("Length: " + builder.length());
//      System.out.println("Type: " + builder.build().getClass());
        return builder.build();
    }

    private ExpressionResolver()
    {
        throw new UnsupportedOperationException();
    }

}
