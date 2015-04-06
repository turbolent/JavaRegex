package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;

import java.util.Objects;

/**
 * The {@code Repetition} pattern matches if the given {@link #pattern pattern} matches at least
 * {@link #min min} and up to {@link #max max} number of input values. Both {@link #min min}
 * and {@link #max max} are limited to {@value #MIN_MAX_LIMIT}.
 * The {@link #greediness greediness} indicates how many occurrences should be matched.
 * <p>
 * The construction methods {@link #repeat(Pattern, int)},
 * {@link #repeat(Pattern, int, Pattern.Greediness)}, {@link #repeat(Pattern, int, int)}, and
 * {@link #repeat(Pattern, int, int, Pattern.Greediness)} are provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Repetition<Value, Result> extends Pattern<Value, Result> {
    private final Pattern<Value, Result> pattern;
    private final Greediness greediness;
    private final int min;
    private final int max;

    /** The limit for {@link #min min} and {@link #max max} */
    public static final int MIN_MAX_LIMIT = 100;

    /**
     * Creates a {@code Repetition} pattern which matches if the given {@code pattern}
     * matches at least {@code min} and up to {@code max} number of input values.
     * Both {@code min} and {@code max} are limited to {@value #MIN_MAX_LIMIT}.
     * The {@code greediness} indicates how many occurrences should be matched.
     *
     * @param pattern     the pattern to be matched
     * @param min         the minimum number of input values the given {@code pattern} should match
     * @param max         the maximum number of input values the given {@code pattern} should match
     * @param greediness  the greediness modifier for the given {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     */
    public Repetition(Pattern<Value, Result> pattern, int min, int max, Greediness greediness) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(greediness);
        this.pattern = pattern;
        min = Math.min(min, MIN_MAX_LIMIT);
        max = Math.min(max, MIN_MAX_LIMIT);
        this.min = Math.min(min, max);
        this.max = max;
        this.greediness = greediness;
    }

    /**
     * Creates a {@code Repetition} pattern which matches if the given {@code pattern}
     * matches at least {@code min} and up to {@code max} number of input values.
     * Both {@code min} and {@code max} are limited to {@value #MIN_MAX_LIMIT}.
     * <p>
     * The greediness for the {@code pattern} is {@link #DEFAULT_GREEDINESS}.
     * *
     * @param pattern  the pattern to be matched
     * @param min      the minimum number of input values the given {@code pattern} should match
     * @param max      the maximum number of input values the given {@code pattern} should match
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public Repetition(Pattern<Value, Result> pattern, int min, int max) {
        this(pattern, min, max, DEFAULT_GREEDINESS);
    }

    @Override
    protected Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        boolean unlimited = this.max == -1;
        ZeroOrMore<Value, Result> zeroOrMore = (unlimited
                                    ? new ZeroOrMore<>(this.pattern, this.greediness)
                                    : null);

        // match exactly min patterns
        Pattern<Value, Result> required = null;
        if (this.min == 0) {
            if (this.max == 0)
                return next;
            else if (unlimited)
                return zeroOrMore.compile(next);
        } else {
            required = this.pattern;
            for (int i = 1; i < this.min; i++)
                required = required.then(this.pattern);
        }

        if (unlimited)
            return required.then(zeroOrMore).compile(next);

        // match optionally (max - min) patterns
        int optionalCount = this.max - this.min;
        Pattern<Value, Result> optional = null;
        if (optionalCount > 0) {
            ZeroOrOne<Value, Result> zeroOrOne = new ZeroOrOne<>(this.pattern);
            optional = zeroOrOne;
            for (int i = 1; i < optionalCount; i++)
                optional = optional.then(zeroOrOne);
        }

        // combine required and optional
        if (required != null && optional != null)
            return required.then(optional).compile(next);
        if (required != null)
            return required.compile(next);
        if (optional != null)
            return optional.compile(next);

        return next;
    }

    @Override
    public String toString() {
        final String max = (this.max == this.min
                            ? ""
                            : ", " + this.max);
        return String.format("Repetition(%s, %s%s, %s)",
                             this.pattern, this.min,
                             max, this.greediness);
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

        Repetition<?, ?> that = (Repetition<?, ?>) other;
        return (Objects.equals(this.min, that.min)
                && Objects.equals(this.max, that.max)
                && Objects.equals(this.pattern, that.pattern)
                && Objects.equals(this.greediness, that.greediness));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.greediness,
                            this.min, this.max);
    }

    /**
     * Convenience construction method for
     * {@link #Repetition(Pattern, int, int, Pattern.Greediness)}
     *
     * @param pattern     the pattern to be matched
     * @param min         the minimum number of input values the given {@code pattern} should match
     * @param max         the maximum number of input values the given {@code pattern} should match
     * @param greediness  the greediness modifier for the given {@code pattern},
     *                    indicating how many occurrences should be matched.

     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code greediness} is {@code null}
     *
     * @return a {@code Repetition} pattern which matches if the given {@code pattern}
     *         matches at least {@code min} and up to {@code max} number of input values.
     *         Both {@code min} and {@code max} are limited to {@value #MIN_MAX_LIMIT}.
     */
    public static <Value, Result> Repetition<Value, Result> repeat
        (Pattern<Value, Result> pattern, int min, int max, Greediness greediness)
    {
        return new Repetition<>(pattern, min, max, greediness);
    }

    /**
     * Convenience construction method for {@link #Repetition(Pattern, int, int)}
     *
     * @param pattern  the pattern to be matched
     * @param min      the minimum number of input values the given {@code pattern} should match
     * @param max      the maximum number of input values the given {@code pattern} should match
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     *
     * @return a {@code Repetition} pattern which matches if the given {@code pattern}
     *         matches at least {@code min} and up to {@code max} number of input values.
     *         Both {@code min} and {@code max} are limited to {@value #MIN_MAX_LIMIT}.
     *         The greediness for the {@code pattern} is {@link #DEFAULT_GREEDINESS}.
     */
    public static <Value, Result> Repetition<Value, Result> repeat
        (Pattern<Value, Result> pattern, int min, int max)
    {
        return new Repetition<>(pattern, min, max);
    }

    /**
     * Convenience construction method for
     * {@link #Repetition(Pattern, int, int, Pattern.Greediness)},
     * where {@code min == max}
     *
     * @param pattern     the pattern to be matched
     * @param n           the number of input values the given {@code pattern} should match
     * @param greediness  the greediness modifier for the given {@code pattern},
     *                    indicating how many occurrences should be matched.
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Repetition} pattern which matches if the given {@code pattern}
     *         matches {@code n} number of input values, which is limited to
     *         {@value #MIN_MAX_LIMIT}.
     */
    public static <Value, Result> Repetition<Value, Result> repeat
        (Pattern<Value, Result> pattern, int n, Greediness greediness)
    {
        return repeat(pattern, n, n, greediness);
    }

    /**
     * Convenience construction method for {@link #Repetition(Pattern, int, int)},
     * where {@code min == max}
     *
     * @param pattern  the pattern to be matched
     * @param n        the number of input values the given {@code pattern} should match
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Repetition} pattern which matches if the given {@code pattern}
     *         matches {@code n} number of input values, which is limited to
     *         {@value #MIN_MAX_LIMIT}. The greediness for the {@code pattern}
     *         is {@link #DEFAULT_GREEDINESS}.
     */
    public static <Value, Result> Repetition<Value, Result> repeat
        (Pattern<Value, Result> pattern, int n)
    {
        return repeat(pattern, n, n);
    }
}
