package solver.testutil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Перехоплення System.out для тестів CLI: увесь вивід йде через System.out.
 */
public final class StdOut {

    private StdOut() {
    }

    /** Виконує дію і повертає все, що вона надрукувала, без пробілів по краях. */
    public static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").trim();
    }
}
