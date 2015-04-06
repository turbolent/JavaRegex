package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Split;

import java.util.Objects;

/**
 * The {@code OneOrMore} pattern matches if the given {@link #pattern pattern} matches
 * one or more input values. The {@link #greediness greediness} indicates how many occurrences
 * should be matched.
 * <p>
 * The construction methods {@link #oneOrMore(Pattern)} and
 * {@link #oneOrMore(Pattern, Pattern.Greediness)} are provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class OneOrMore<Value, Result> extends Pattern<Value, Result> {
    private final Pattern<Value, Result> pattern;
    private final Greediness greediness;

    /**
     * Creates an {@code OneOrMore} pattern which matches if the given {@code pattern}
     * matches one or more input values. The {@code greediness} indicates how many
     * occurrences should be matched.
     *
     * @param pattern     the pattern to be matched
     * @param greediness  the greediness modifier for the given {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     */
    public OneOrMore(Pattern<Value, Result> pattern, Greediness greediness) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(greediness);
        this.pattern = pattern;
        this.greediness = greediness;
    }

    /**
     * Creates an {@code OneOrMore} pattern which matches if the given {@code pattern}
     * matches one or more input values.
     * <p>
     * The greediness for the {@code pattern} is {@link #DEFAULT_GREEDINESS}.
     *
     * @param pattern  the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public OneOrMore(Pattern<Value, Result> pattern) {
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
                return code;
            }
            case LAZY: {
                split.next = next;
                split.split = code;
                return code;
            }
            default:
                throw Greediness.newUnsupportedException(this.greediness);
        }
    }

    @Override
    public String toString() {
        return String.format("OneOrMore(%s, %s)",
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

        OneOrMore<?, ?> oneOrMore = (OneOrMore<?, ?>) other;
        return (Objects.equals(this.pattern, oneOrMore.pattern)
                && Objects.equals(this.greediness, oneOrMore.greediness));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.greediness);
    }

    /**
     * Convenience construction method for {@link #OneOrMore(Pattern, Pattern.Greediness)}
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
     * @return an {@code OneOrMore} pattern which matches if the given {@code pattern}
     *         matches one or more input values
     */
    public static <Value, Result> OneOrMore<Value, Result> oneOrMore
        (Pattern<Value, Result> pattern, Greediness greediness)
    {
        return new OneOrMore<>(pattern, greediness);
    }

    /**
     * Convenience construction method for {@link #OneOrMore(Pattern)}
     *
     * @param pattern  the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     *
     * @return an {@code OneOrMore} pattern which matches if the given {@code pattern}
     *         matches one or more input values
     */
    public static <Value, Result> OneOrMore<Value, Result> oneOrMore
        (Pattern<Value, Result> pattern)
    {
        return new OneOrMore<>(pattern);
    }
}
