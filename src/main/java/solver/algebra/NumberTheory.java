package solver.algebra;

import java.math.BigInteger;
import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * ALG Lab 2: max3, НСД, факторіал, Фібоначчі.
 */
public final class NumberTheory {

    private NumberTheory() {
    }

    public static double max3(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    public static void solveGcd(long a, long b) {
        if (a == 0L && b == 0L) {
            printResult(0L);
            return;
        }
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger res = b1.gcd(b2);
        printResult(res.longValue());
    }

    public static void factorial(long n) {
        if (n < 0L || n > 20L) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        long result = 1L;
        for (long i = 2L; i <= n; i++) {
            result *= i;
        }
        printResult(result);
    }

    public static void fibonacci(long n) {
        if (n < 0L || n > 92L) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (n == 0L) {
            printResult(0L);
            return;
        }
        long prev = 0L;
        long curr = 1L;
        for (long i = 2L; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        printResult(curr);
    }
}
