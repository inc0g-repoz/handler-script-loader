package test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.function.Supplier;

import com.github.inc0grepoz.ltse.FlowControl;
import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.ScriptExecutor;

@SuppressWarnings({"rawtypes", "unused"})
public class Test
{

    public static void main(String[] args)
    {
        testScriptEngine();
    }

    private static void testScriptEngine()
    {
        ScriptExecutor executor = new ScriptExecutor();

        // src/com/github/inc0grepoz/dsl/util/Lexer.java
        // test
        File file = new File("test");
        Supplier[] getter = (Supplier[]) Array.newInstance(Supplier.class, 1);

        Script script = time("Compiled", () -> {
            try
            {
                return executor.load(file);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });

        Object rv = time("Executed", () -> script.callFunction("main"));

        if (rv != FlowControl.VOID)
        {
            System.out.println("Returned " + rv);
        }
    }

    private static <T> T time(Supplier<T> lambda)
    {
        return time(null, lambda);
    }

    private static <T> T time(String label, Supplier<T> lambda)
    {
        long time = System.currentTimeMillis();
        T rv = lambda.get();
        time = System.currentTimeMillis() - time;
        System.out.println((label == null ? "" : label + " in ") + time + " ms");
        return rv;
    }

}
