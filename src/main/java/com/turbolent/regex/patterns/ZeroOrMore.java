package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Split;

import java.util.Objects;

/**
 * The {@code ZeroOrMore} pattern matches if the given {@link #pattern pattern} matches
 * zero or more input values. The {@link #greediness greediness} indicates how many occurrences
 * should be matched.
 * <p>
 * The construction methods {@link #zeroOrMore(Pattern)} and
 * {@link #zeroOrMore(Pattern, Pattern.Greediness)} are provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class ZeroOrMore<Value, Result> extends Pattern<Value, Result> {
    private final Pattern<Value, Result> pattern;
    private final Greediness greediness;

    /**
     * Creates a {@code ZeroOrMore} pattern which matches if the given {@code pattern} matches
     * zero or more input values. The {@code greediness} indicates how many occurrences
     * should be matched.
     *
     * @param pattern     the pattern to be matched
     * @param greediness  the greediness modifier for the given {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     */
    public ZeroOrMore(Pattern<Value, Result> pattern, Greediness greediness) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(greediness);
        this.pattern = pattern;
        this.greediness = greediness;
    }

    /**
     * Creates a {@code ZeroOrMore} pattern which matches if the given {@code pattern} matches
     * zero or more input values.
     * <p>
     * The greediness for the {@code pattern} is {@link #DEFAULT_GREEDINESS}.
     *
     * @param pattern  the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public ZeroOrMore(Pattern<Value, Result> pattern) {
        this(pattern, DEFAULT_GREEDINESS);
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        final Split<Value, Result> split = new Split<>(null, null);
        final Instruction<Value, Result> code = this.pattern.compile(split);
        switch (this.greediness) {
            case GREEDY: {
                split.next = code;
                split.split = next;
                return split;
            }
            case LAZY: {
                split.next = next;
                split.split = code;
                return split;
            }
            default:
                throw Greediness.newUnsupportedException(this.greediness);
        }
    }

    @Override
    public String toString() {
        return String.format("ZeroOrMore(%s, %s)",
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

        ZeroOrMore<?, ?> zeroOrMore = (ZeroOrMore<?, ?>) other;
        return (Objects.equals(this.pattern, zeroOrMore.pattern)
                && Objects.equals(this.greediness, zeroOrMore.greediness));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.greediness);
    }

    /**
     * Convenience construction method for {@link #ZeroOrMore(Pattern, Pattern.Greediness)}
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
     * @return a {@code ZeroOrMore} pattern which matches if the given {@code pattern} matches
     *         zero or more input values.
     */
    public static <Value, Result> ZeroOrMore<Value, Result> zeroOrMore
        (Pattern<Value, Result> pattern, Greediness greediness)
    {
        return new ZeroOrMore<>(pattern, greediness);
    }

    /**
     * Convenience construction method for {@link #ZeroOrMore(Pattern)}
     *
     * @param pattern  the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     *
     * @return a {@code ZeroOrMore} pattern which matches if the given {@code pattern} matches
     *         zero or more input values. The greediness for the {@code pattern} is
     *         {@link #DEFAULT_GREEDINESS}.
     */
    public static <Value, Result> ZeroOrMore<Value, Result> zeroOrMore
        (Pattern<Value, Result> pattern)
    {
        return new ZeroOrMore<>(pattern);
    }
}
