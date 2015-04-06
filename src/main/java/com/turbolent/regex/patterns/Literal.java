package com.turbolent.regex.patterns;

import java.util.Objects;

/**
 * The {@code Literal} pattern matches if the current input value
 * is the given {@link #value value}, which may be {@code null}.
 * <p>
 * The construction method {@link #literal(Object)} is provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */

public class Literal<Value, Result> extends Test<Value, Result> {
    private final Value value;

    /**
     * Creates a {@code Literal} pattern which matches if the current input value
     * is the given {@code value}.
     *
     * @param value  the value to compare with the current input value for equality.
     *               May be {@code null}
     */
    public Literal(Value value) {
        super(inputValue -> Objects.equals(value, inputValue));
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Literal(%s)", this.value);
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

        final Literal<?, ?> literal = (Literal<?, ?>) other;
        return Objects.equals(this.value, literal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Convenience construction method for {@link #Literal(Object)}
     *
     * @param value  the value to compare with the current input value for equality.
     *               May be {@code null}
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Literal} pattern which matches if the current input value
     *         is the given {@code value}
     */
    public static <Value, Result> Literal<Value, Result> literal(Value value) {
        return new Literal<>(value);
    }
}
