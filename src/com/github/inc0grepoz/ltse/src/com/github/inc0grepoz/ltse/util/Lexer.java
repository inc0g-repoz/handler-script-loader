package com.github.inc0grepoz.ltse.util;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.inc0grepoz.ltse.exception.SyntaxError;

public class Lexer
{

    private static final String CHAR_DIGIT = ".0123456789";
    private static final String CHAR_BRACES = "()[]{}<>";
    private static final String CHAR_SPECIAL = "\r\t\n ~!@#$%^&*()-=+[]{}:;'\"\\|/<>,.?";
    private static final String CHAR_NEVER_TRAIL = CHAR_BRACES + "!;.";
    private static final Pattern PATTERN_NUMBER = Pattern.compile("(\\d*\\.)?(\\d+)");

    public static LinkedList<String> readTokens(Reader in) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        LinkedList<String> tokens = new LinkedList<>();

        int i;                       // raw character
        char ch, chp = '\0';         // current and previous characters
        char q = '\0';               // quotes used: '\0', '\'', '"'
        boolean lastSpecial = false; // last token was a special character
        boolean escapeNext = false;  // '\' tokens for strings

        /*
         * 0 - not commenting
         * 1 - single-line
         * 2 - multi-line
         */
        byte cmt = 0;

        // Reading the whole input
        while ((i = in.read()) != -1)
        {
            ch = (char) i;

            if (ch == '\r')
            {
                continue; // carriage return
            }

            // Comment end
            if (cmt != 0)
            {
                if (cmt == 2 && ch == '/' && chp == '*'
                        || cmt == 1 && ch == '\n')
                {
                    cmt = 0;
                    chp = '\0';
                }
                else
                {
                    chp = ch;
                }

                continue;
            }

            // Writing non-special characters
            if (CHAR_SPECIAL.indexOf(ch) == -1)
            {
                if (lastSpecial)
                {
                    flushBuffer(builder, tokens);
                }

                escapeNext = false;
                lastSpecial = false;
                builder.append(chp = ch);

                continue;
            }

            // Escaping the next character, if necessary
            if (ch == '\\' && q != '\0')
            {
                escapeNext = !escapeNext;
                builder.append(chp = ch);
                continue;
            }

            // Quotes
            if (ch == '\'' || ch == '"')
            {
                if (escapeNext)
                {
                    escapeNext = false;
                    builder.append(ch);
                    continue;
                }
                else if (q == '\0')
                {
                    q = ch;
                    lastSpecial = false; // not resolved along with special symbols

                    // Writing the last token before opening
                    flushBuffer(builder, tokens);
                }
                else if (q == ch)
                {
                    q = '\0';
                }

                // String tokens are enclosed in quotes
                builder.append(chp = ch);
                continue;
            }

            // Whitespaces
            if (Character.isWhitespace(ch))
            {
                if (q == '\0') // no quotes
                {
                    flushBuffer(builder, tokens);
                }
                else
                {
                    builder.append(chp = ch);
                }
                continue;
            }

            // Comment start
            if (ch == '*' && chp == '/' && q == '\0')
            {
                cmt = 2;
                chp = '\0';
                builder.setLength(0);
                continue;
            }

            // Comment start
            if (ch == '/' && chp == '/' && q == '\0')
            {
                cmt = 1;
                builder.setLength(0);
                continue;
            }

            if (q != '\0')
            {
                builder.append(chp = ch);
                continue;
            }

            // Number are whole tokens and literals are written with the
            // non-special characters
            if (CHAR_DIGIT.indexOf(ch) != -1)
            {
                if (CHAR_DIGIT.indexOf(chp) != -1);
                {
                    // Distinguishing dots between variables and method calls
                    if (PATTERN_NUMBER.matcher(builder.toString()).matches())
                    {
                        builder.append(chp = ch);
                        continue;
                    }
                }
            }

            // Some special symbols may trail
            if (!lastSpecial
                    || CHAR_NEVER_TRAIL.indexOf(ch) != -1
                    || CHAR_BRACES.indexOf(chp) != -1)
            {
                flushBuffer(builder, tokens);
            }

            lastSpecial = true;
            builder.append(chp = ch);
        }

        // Reading the last token
        flushBuffer(builder, tokens);

        // Validate all quotes and multi-line comments terminated
        if (q != '\0')   throw new SyntaxError("Unterminated quote detected");
        if (cmt == '\2') throw new SyntaxError("Unterminated multi-line comment detected");

        return tokens;
    }

    private static void flushBuffer(StringBuilder builder, List<String> tokens)
    {
        if (builder.length() != 0)
        {
            tokens.add(builder.toString());
            builder.setLength(0);
        }
    }

}
