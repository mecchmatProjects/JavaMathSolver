package solver.algebra;

import java.util.Collections;
import java.util.Map;

/**
 * Базовий абстрактний клас для математичних виразів (Task A11).
 * Служить кореневим типом для ієрархії виразів (константи, змінні, бінарні операції).
 */
public abstract class Expression {

    /**
     * Обчислює значення виразу для переданого контексту змінних (Task A15).
     *
     * @param variables карта пар "ім'я змінної" -> "значення"
     * @return обчислене числове значення виразу
     */
    public abstract double evaluate(Map<String, Double> variables);

    /**
     * Зручний метод обчислення для виразів без змінних (константних).
     *
     * @return обчислене значення
     */
    public double evaluate() {
        return evaluate(Collections.emptyMap());
    }

    /**
     * Повертає рядкове математичне представлення виразу.
     */
    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}