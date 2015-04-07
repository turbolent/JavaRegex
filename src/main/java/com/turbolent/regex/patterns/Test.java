package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Atom;
import com.turbolent.regex.instructions.Instruction;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The {@code Test} pattern matches if testing the {@link #predicate predicate}
 * with the current input value succeeds.
 * <p>
 * The construction method {@link #test(java.util.function.Predicate)}
 * is provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Test<Value, Result> extends Pattern<Value, Result> {

    /** The predicate used for testing the current input value */
    protected final Predicate<Value> predicate;

    /**
     * Creates a {@code Test} pattern which matches if testing the {@code predicate}
     * with the current input value succeeds.
     *
     * @param predicate  the predicate to use for testing the current input value
     *
     * @throws NullPointerException
     *         if {@code predicate} is {@code null}
     */
    public Test(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        return new Atom<>(this.predicate, next);
    }

    @Override
    public String toString() {
        return String.format("Test(%s)", this.predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null
            || getClass() != other.getClass())
        {
            return false;
        }

        Test<?, ?> test = (Test<?, ?>) other;
        return Objects.equals(this.predicate, test.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.predicate);
    }

    /**
     * Convenience construction method for {@link #Test(java.util.function.Predicate)}
     *
     * @param predicate  the predicate to use for testing the current input value
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@link Test} pattern testing the current input value with {@code predicate}
     *
     * @throws NullPointerException
     *         if {@code predicate} is {@code null}
     */
    public static <Value, Result> Test<Value, Result> test(Predicate<Value> predicate) {
        return new Test<>(predicate);
    }
}
