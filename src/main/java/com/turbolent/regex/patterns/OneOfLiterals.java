package com.turbolent.regex.patterns;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The {@code OneOfLiterals} pattern matches if the current input value
 * is equal to any of the given {@link #values values}.
 * <p>
 * Use {@link #oneOfLiterals(Object, Object[])} to obtain an alternating pattern.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class OneOfLiterals<Value, Result> extends Test<Value, Result> {

    /** The values to compare with the current input value for equality */
    private final Set<Value> values;

    /**
     * Creates an {@code OneOfLiterals} pattern which matches if the current input value
     * is equal to any of the given {@code values}.
     *
     * @param values  the values to compare with the current input value for equality
     *
     * @throws NullPointerException
     *         if {@code values} is {@code null}
     */
    private OneOfLiterals(Set<Value> values) {
        super(values::contains);
        Objects.requireNonNull(values);
        this.values = values;
    }

    @Override
    public String toString() {
        return String.format("OneOfLiterals(%s)", this.values);
    }

    /**
     * Construction method for {@link #OneOfLiterals(java.util.Set)}
     * which ensures there is at least one value to compare to
     *
     * @param value        the first value to compare with the current input value for equality.
     *                     May be {@code null}
     * @param otherValues  the other value sto compare with the current input value for equality.
     *                     THe values may be {@code null}
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return an {@code OneOfLiterals} pattern which matches if the current input value
     *         is equal to {@code value} or any of the values in {@code otherValues}.
     */
    @SafeVarargs
    public static <Value, Result> Pattern<Value, Result> oneOfLiterals
        (Value value, Value... otherValues)
    {
        if (otherValues.length == 0)
            return new Literal<>(value);

        Set<Value> values = Stream.concat(Stream.of(value),
                                          Arrays.stream(otherValues))
                                  .collect(orderedSetCollector());
        return new OneOfLiterals<>(values);
    }
}
