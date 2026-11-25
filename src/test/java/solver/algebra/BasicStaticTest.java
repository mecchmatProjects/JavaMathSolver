package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicStaticTest {

    private static final double EPS = 1e-9;

    private static final double[] DATA = {2, 4, 4, 4, 5, 5, 7, 9};

    //variance(генеральна)
    @Test
    @DisplayName("variance: класичний приклад, σ² = 32 / 8 = 4")
    void varianceClassic() {
        assertEquals(4.0, BasicStatic.variance(DATA), EPS);
    }

    @Test
    @DisplayName("variance: {1,2,3,4,5} → 10 / 5 = 2")
    void varianceOneToFive() {
        assertEquals(2.0, BasicStatic.variance(new double[]{1, 2, 3, 4, 5}), EPS);
    }

    @Test
    @DisplayName("variance: {1,2} → 0.5 / 2 = 0.25")
    void varianceTwoElements() {
        assertEquals(0.25, BasicStatic.variance(new double[]{1, 2}), EPS);
    }

    @Test
    @DisplayName("variance: null, порожній масив та один елемент дають 0")
    void varianceDegenerate() {
        assertEquals(0.0, BasicStatic.variance(null), EPS);
        assertEquals(0.0, BasicStatic.variance(new double[]{}), EPS);
        assertEquals(0.0, BasicStatic.variance(new double[]{42}), EPS);
    }

    @Test
    @DisplayName("variance: однакові елементи дають 0")
    void varianceConstant() {
        assertEquals(0.0, BasicStatic.variance(new double[]{3, 3, 3, 3}), EPS);
    }

    @Test
    @DisplayName("variance: від'ємні числа {-1, 1} → 1")
    void varianceNegative() {
        assertEquals(1.0, BasicStatic.variance(new double[]{-1, 1}), EPS);
    }

    //sampleVariance(вибіркова, незміщена)

    @Test
    @DisplayName("sampleVariance: 32 / 7")
    void sampleVarianceClassic() {
        assertEquals(32.0 / 7.0, BasicStatic.sampleVariance(DATA), EPS);
    }

    @Test
    @DisplayName("sampleVariance: {1,2,3,4,5} → 10 / 4 = 2.5")
    void sampleVarianceOneToFive() {
        assertEquals(2.5, BasicStatic.sampleVariance(new double[]{1, 2, 3, 4, 5}), EPS);
    }

    @Test
    @DisplayName("sampleVariance: {1,2} → 0.5")
    void sampleVarianceTwoElements() {
        assertEquals(0.5, BasicStatic.sampleVariance(new double[]{1, 2}), EPS);
    }

    @Test
    @DisplayName("sampleVariance: null, порожній масив та один елемент дають 0 (ділення на n-1 неможливе)")
    void sampleVarianceDegenerate() {
        assertEquals(0.0, BasicStatic.sampleVariance(null), EPS);
        assertEquals(0.0, BasicStatic.sampleVariance(new double[]{}), EPS);
        assertEquals(0.0, BasicStatic.sampleVariance(new double[]{42}), EPS);
    }

    @Test
    @DisplayName("sampleVariance = variance · n / (n − 1)")
    void sampleVsPopulationRelation() {
        int n = DATA.length;
        assertEquals(BasicStatic.variance(DATA) * n / (n - 1),
                BasicStatic.sampleVariance(DATA), EPS);
    }

    //standardDeviation

    @Test
    @DisplayName("standardDeviation: класичний приклад, σ = 2")
    void standardDeviationClassic() {
        assertEquals(2.0, BasicStatic.standardDeviation(DATA), EPS);
    }

    @Test
    @DisplayName("standardDeviation: {1,2,3,4,5} → √2")
    void standardDeviationOneToFive() {
        assertEquals(Math.sqrt(2), BasicStatic.standardDeviation(new double[]{1, 2, 3, 4, 5}), EPS);
    }

    @Test
    @DisplayName("standardDeviation: вироджені випадки дають 0")
    void standardDeviationDegenerate() {
        assertEquals(0.0, BasicStatic.standardDeviation(null), EPS);
        assertEquals(0.0, BasicStatic.standardDeviation(new double[]{}), EPS);
        assertEquals(0.0, BasicStatic.standardDeviation(new double[]{7}), EPS);
        assertEquals(0.0, BasicStatic.standardDeviation(new double[]{5, 5, 5}), EPS);
    }

    //sampleStandardDeviation

    @Test
    @DisplayName("sampleStandardDeviation: √(32/7)")
    void sampleStandardDeviationClassic() {
        assertEquals(Math.sqrt(32.0 / 7.0), BasicStatic.sampleStandardDeviation(DATA), EPS);
    }

    @Test
    @DisplayName("sampleStandardDeviation: {-1, 1} → √2")
    void sampleStandardDeviationTwoElements() {
        assertEquals(Math.sqrt(2), BasicStatic.sampleStandardDeviation(new double[]{-1, 1}), EPS);
    }

    @Test
    @DisplayName("sampleStandardDeviation: вироджені випадки дають 0")
    void sampleStandardDeviationDegenerate() {
        assertEquals(0.0, BasicStatic.sampleStandardDeviation(null), EPS);
        assertEquals(0.0, BasicStatic.sampleStandardDeviation(new double[]{}), EPS);
        assertEquals(0.0, BasicStatic.sampleStandardDeviation(new double[]{7}), EPS);
        assertEquals(0.0, BasicStatic.sampleStandardDeviation(new double[]{5, 5, 5}), EPS);
    }

    //властивості

    @Test
    @DisplayName("Інваріантність до зсуву: Var(X + c) = Var(X)")
    void shiftInvariance() {
        double[] shifted = new double[DATA.length];
        for (int i = 0; i < DATA.length; i++) {
            shifted[i] = DATA[i] + 100;
        }
        assertEquals(BasicStatic.variance(DATA), BasicStatic.variance(shifted), EPS);
        assertEquals(BasicStatic.sampleVariance(DATA), BasicStatic.sampleVariance(shifted), EPS);
    }

    @Test
    @DisplayName("Масштабування: Var(kX) = k² · Var(X)")
    void scaling() {
        double k = 3.0;
        double[] scaled = new double[DATA.length];
        for (int i = 0; i < DATA.length; i++) {
            scaled[i] = k * DATA[i];
        }
        assertEquals(k * k * BasicStatic.variance(DATA), BasicStatic.variance(scaled), EPS);
        assertEquals(k * BasicStatic.standardDeviation(DATA), BasicStatic.standardDeviation(scaled), EPS);
    }

    @Test
    @DisplayName("Порядок елементів не впливає на результат")
    void orderIndependence() {
        double[] reversed = {9, 7, 5, 5, 4, 4, 4, 2};
        assertEquals(BasicStatic.variance(DATA), BasicStatic.variance(reversed), EPS);
    }

    @Test
    @DisplayName("Чисельна стабільність: великі значення з малим розкидом")
    void numericalStability() {
        double[] big = {1e9 + 4, 1e9 + 7, 1e9 + 13, 1e9 + 16};
        assertEquals(22.5, BasicStatic.variance(big), 1e-6);
        assertEquals(30.0, BasicStatic.sampleVariance(big), 1e-6);
    }

    @Test
    @DisplayName("Дисперсія та стандартні відхилення невід'ємні")
    void nonNegative() {
        double[] data = {-3.5, 2.1, 0, 8.8, -1.2, 4.4};
        assertTrue(BasicStatic.variance(data) >= 0);
        assertTrue(BasicStatic.sampleVariance(data) >= 0);
        assertTrue(BasicStatic.standardDeviation(data) >= 0);
        assertTrue(BasicStatic.sampleStandardDeviation(data) >= 0);
    }

    @Test
    @DisplayName("Вхідний масив не змінюється")
    void doesNotMutateInput() {
        double[] copy = DATA.clone();
        BasicStatic.variance(DATA);
        BasicStatic.sampleVariance(DATA);
        BasicStatic.standardDeviation(DATA);
        BasicStatic.sampleStandardDeviation(DATA);
        assertArrayEquals(copy, DATA, 0.0);
    }

    @Test
    @DisplayName("NaN у даних дає NaN")
    void nanPropagates() {
        double[] withNaN = {1, Double.NaN, 3};
        assertTrue(Double.isNaN(BasicStatic.variance(withNaN)));
        assertTrue(Double.isNaN(BasicStatic.sampleVariance(withNaN)));
        assertTrue(Double.isNaN(BasicStatic.standardDeviation(withNaN)));
        assertTrue(Double.isNaN(BasicStatic.sampleStandardDeviation(withNaN)));
    }
}