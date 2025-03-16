package com.github.inc0grepoz.ltse.util;

import java.util.LinkedList;
import java.util.ListIterator;

import com.github.inc0grepoz.ltse.exception.SyntaxError;

public class TokenHelper
{

    public static LinkedList<String> readEnclosedTokens(LinkedList<String> tokens,
            String prefix, String suffix)
    {
        LinkedList<String> out = new LinkedList<>();
        String token = tokens.poll();
        int level = 1;

        if (!prefix.equals(token))
        {
            throw new SyntaxError("\"" + prefix + "\" expected, but \"" + token + "\" found");
        }

        while (true)
        {
            token = tokens.poll();

            if (token == null)
            {
                throw new SyntaxError("Unterminated sequence in " + prefix + suffix);
            }
            else if (prefix.equals(token))
            {
                level++;
            }
            else if (suffix.equals(token))
            {
                level--;
            }

            if (level == 0)
            {
                break;
            }

            out.add(token);
        }

        return out;
    }

    public static LinkedList<String> readEnclosedTokensBackwards(LinkedList<String> tokens,
            String prefix, String suffix)
    {
        LinkedList<String> out = new LinkedList<>();
        String token = tokens.pollLast();
        int level = 1;

        if (!suffix.equals(token))
        {
            throw new SyntaxError("\"" + suffix + "\" expected, but \"" + token + "\" found");
        }

        while (true)
        {
            token = tokens.pollLast();

            if (token == null)
            {
                throw new SyntaxError("Unterminated sequence in " + prefix + suffix);
            }
            else if (suffix.equals(token))
            {
                level++;
            }
            else if (prefix.equals(token))
            {
                level--;
            }

            if (level == 0)
            {
                break;
            }

            out.addFirst(token);
        }

        return out;
    }

    public static LinkedList<LinkedList<String>> splitTokens(LinkedList<String> tokens, String separator)
    {
        int parentheses = 0;
        int squareBrackets = 0;

        LinkedList<LinkedList<String>> separateTokens = new LinkedList<>();
    
        ListIterator<String> iter = tokens.listIterator();
        LinkedList<String> tokenList = new LinkedList<>();
        String next;
    
        while (iter.hasNext())
        {
            switch (next = iter.next())
            {
            case "[":
                squareBrackets++;
                break;
            case "]":
                squareBrackets--;
                break;
            case "(":
                parentheses++;
                break;
            case ")":
                parentheses--;
                break;
            }

            if (squareBrackets == 0 && parentheses == 0 && next.equals(separator))
            {
                // Flushing the previous tokens
                separateTokens.add(tokenList);
                tokenList = new LinkedList<>();
            }
            else
            {
                // Writing the observed token
                tokenList.add(next);
            }
        }
    
        // Flushing the last sequence
        separateTokens.add(tokenList);
    
        return separateTokens;
    }

    public static LinkedList<String> openParentheses(LinkedList<String> tokens)
    {
        int balance;

        while (tokens.size() > 1
                && tokens.getFirst().equals("(")
                && tokens.getLast().equals(")"))
        {
            tokens.removeFirst();
            tokens.removeLast();
            balance = 0;

            for (String token: tokens)
            {
                switch (token)
                {
                case "(":
                    balance++;
                    break;
                case ")":
                    balance--;
                    break;
                }

                if (balance < 0)
                {
                    tokens.addFirst("(");
                    tokens.addLast(")");

                    return tokens;
                }
            }
        }

        return tokens;
    }

    private TokenHelper()
    {
        throw new UnsupportedOperationException();
    }

}
