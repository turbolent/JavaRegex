package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Split;

import java.util.Objects;

/**
 * The {@code ZeroOrOne} pattern matches if the given {@link #pattern pattern} optionally
 * matches one input value. The {@link #greediness greediness} indicates how many occurrences
 * should be matched.
 * <p>
 * The construction methods {@link #zeroOrOne(Pattern)} and
 * {@link #zeroOrOne(Pattern, Pattern.Greediness)} are provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class ZeroOrOne<Value, Result> extends Pattern<Value, Result> {
    private final Pattern<Value, Result> pattern;
    private final Greediness greediness;

    /**
     * Creates a {@code ZeroOrOne} pattern which matches if the given {@code pattern} matches
     * zero or one input value. The {@code greediness} indicates how many occurrences
     * should be matched.
     *
     * @param pattern     the pattern to be matched
     * @param greediness  the greediness modifier for the given {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     */
    public ZeroOrOne(Pattern<Value, Result> pattern, Greediness greediness) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(greediness);
        this.pattern = pattern;
        this.greediness = greediness;
    }

    /**
     * Creates a {@code ZeroOrOne} pattern which matches if the given {@code pattern} matches
     * zero or one input value.
     * <p>
     * The greediness for the {@code pattern} is {@link #DEFAULT_GREEDINESS}.
     *
     * @param pattern  the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public ZeroOrOne(Pattern<Value, Result> pattern) {
        this(pattern, DEFAULT_GREEDINESS);
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        final Instruction<Value, Result> code = this.pattern.compile(next);
        switch (this.greediness) {
            case GREEDY:
                return new Split<>(code, next);
            case LAZY:
                return new Split<>(next, code);
            default:
                throw Greediness.newUnsupportedException(this.greediness);
        }
    }

    @Override
    public String toString() {
        return String.format("ZeroOrOne(%s, %s)",
                             this.pattern, this.greediness);
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

        ZeroOrOne<?, ?> zeroOrOne = (ZeroOrOne<?, ?>) other;
        return (Objects.equals(this.pattern, zeroOrOne.pattern)
                && Objects.equals(this.greediness, zeroOrOne.greediness));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.greediness);
    }

    /**
     * Convenience construction method for {@link #ZeroOrOne(Pattern, Pattern.Greediness)}
     *
     * @param pattern     the pattern to be matched
     * @param greediness  the greediness modifier for the given {@code pattern},
     *                    indicating how many occurrences should be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     *
     * @return a {@code ZeroOrOne} pattern which matches if the given {@code pattern} matches
     *         zero or one input value.
     */
    public static <Value, Result> ZeroOrOne<Value, Result> zeroOrOne
        (Pattern<Value, Result> pattern, Greediness greediness)
    {
        return new ZeroOrOne<>(pattern, greediness);
    }

    /**
     * Convenience construction method for {@link #ZeroOrOne(Pattern)}
     *
     * @param pattern  the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     *
     * @return a {@code ZeroOrOne} pattern which matches if the given {@code pattern} matches
     *         zero or one input value.
     */
    public static <Value, Result> ZeroOrOne<Value, Result> zeroOrOne
        (Pattern<Value, Result> pattern)
    {
        return new ZeroOrOne<>(pattern);
    }
}
