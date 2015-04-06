package com.turbolent.regex.patterns;

import com.turbolent.regex.Parser;
import com.turbolent.regex.PartialMatch;
import com.turbolent.regex.instructions.Accept;
import com.turbolent.regex.instructions.Instruction;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * The root class in the <i>pattern hierarchy</i>.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public abstract class Pattern<Value, Result> {

    /**
     * The greediness for repeating {@linkplain Pattern patterns}.
     */
    public enum Greediness {
        /** match as many occurrences as possible (longest match) */
        GREEDY,
        /** match as few occurrences as possible (shortest match) */
        LAZY;

        protected static UnsupportedOperationException newUnsupportedException
            (Greediness greediness)
        {
            final String message = String.format("Unsupported greediness: %s", greediness);
            return new UnsupportedOperationException(message);
        }
    }

    /** The default {@link Greediness} for repeating patterns */
    public static final Greediness DEFAULT_GREEDINESS = Greediness.GREEDY;

    /**
     * Compiles the given {@code pattern} into
     * {@linkplain com.turbolent.regex.instructions.Instruction matching instructions}
     *
     * @param pattern  the pattern to be compiled into
     *                 {@linkplain com.turbolent.regex.instructions.Instruction
     *                 matching instructions}
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return the start instruction of the compiled pattern
     */
    public static <Value, Result> Instruction<Value, Result> compile
        (Pattern<Value, Result> pattern)
    {
        return pattern.compile(Accept.accept());
    }

    protected abstract Instruction<Value, Result> compile(Instruction<Value, Result> next);

    /**
     * Convenience construction method for
     * {@link Alternation#Alternation(java.util.List)}
     *
     * @param other  the other pattern to be matched
     *
     * @return an {@code Alternation} pattern of {@code this} and {@code other}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code other} is null
     */
    public final Pattern<Value, Result> or(Pattern<Value, Result> other) {
        return Alternation.alternation(this, other);
    }

    /**
     * Convenience construction method for
     * {@link Concatenation#Concatenation(java.util.List)}
     *
     * @param other  the other pattern to be matched
     *
     * @return a {@code Concatenation} pattern of {@code this} and {@code other}
     *
     * @throws NullPointerException
     *         if {@code pattern} or {@code other} is null
     */
    public final Pattern<Value, Result> then(Pattern<Value, Result> other) {
        return Concatenation.concatenation(this, other);
    }

    /**
     * Convenience construction method for
     * {@link Repetition#Repetition(Pattern, int, int, Pattern.Greediness)}
     *
     * @param min         the minimum number of input values this pattern should match
     * @param max         the maximum number of input values this pattern should match
     * @param greediness  the greediness modifier for the given {@code pattern}
     *
     * @return a repeating pattern of {@code this}
     */
    public final Repetition<Value, Result> repeat(int min, int max, Greediness greediness) {
        return new Repetition<>(this, min, max, greediness);
    }

    /**
     * Convenience construction method for {@link Repetition#Repetition(Pattern, int, int)}
     *
     * @param min  the minimum number of input values the this pattern should match
     * @param max  the maximum number of input values the this pattern should match
     *
     * @return a repeating pattern of {@code this}
     */
    public final Repetition<Value, Result> repeat(int min, int max) {
        return new Repetition<>(this, min, max);
    }

    /**
     * Convenience construction method for
     * {@link Repetition#Repetition(Pattern, int, int, Pattern.Greediness)},
     * where {@code min == max}
     *
     * @param n           the number of input values the this pattern should match
     * @param greediness  the greediness modifier for the this pattern
     *
     * @return a repeating pattern of {@code this}
     */
    public final Repetition<Value, Result> repeat(int n, Greediness greediness) {
        return repeat(n, n, greediness);
    }

    /**
     * Convenience construction method for {@link Repetition#Repetition(Pattern, int, int)},
     * where {@code min == max}
     *
     * @param n  the number of input values the this pattern should match
     *
     * @return a repeating pattern of {@code this}
     */
    public final Repetition<Value, Result> repeat(int n) {
        return repeat(n, n);
    }

    /**
     * Convenience construction method for {@link Captured#Captured(Object, Pattern)}
     *
     * @param identifier  the name of the group used when storing the values matching
     *                    this pattern in the {@linkplain com.turbolent.regex.Match match}.
     *                    May be {@code null}
     *
     * @return a {@code Captured} pattern of {@code this}
     */
    public final Captured<Value, Result> capture(Object identifier) {
        return new Captured<>(identifier, this);
    }

    /**
     * Convenience construction method for
     * {@link Call#Call(java.util.function.BiConsumer, Pattern)}
     *
     * @param consumer  the consumer to be invoked with the
     *                  {@linkplain com.turbolent.regex.Parser parser} and the
     *                  {@linkplain PartialMatch partial match}
     *
     * @throws NullPointerException
     *         if {@code consumer} is {@code null}
     *
     * @return a {@code Call} pattern of {@code this}
     */
    public final Call<Value, Result> call
        (BiConsumer<Parser, PartialMatch<Value, Result>> consumer)
    {
        return new Call<>(consumer, this);
    }

    /**
     * Convenience construction method for
     * {@link Call#Call(java.util.function.Consumer, Pattern)}
     *
     * @param consumer  the consumer to be invoked with the {@linkplain PartialMatch partial match}
     *
     * @throws NullPointerException
     *         if {@code consumer} is {@code null}
     *
     * @return a {@code Call} pattern of {@code this}
     */
    public final Call<Value, Result> call(Consumer<PartialMatch<Value, Result>> consumer) {
        return new Call<>(consumer, this);
    }

    /**
     * Convenience construction method for
     * {@link ZeroOrMore#ZeroOrMore(Pattern, Pattern.Greediness)}
     *
     * @param greediness  the greediness modifier for this pattern
     *
     * @throws NullPointerException
     *         if {@code greediness} is {@code null}
     *
     * @return a {@code ZeroOrMore} pattern of {@code this}
     */
    public final ZeroOrMore<Value, Result> zeroOrMore(Greediness greediness) {
        return new ZeroOrMore<>(this, greediness);
    }

    /**
     * Convenience construction method for {@link ZeroOrMore#ZeroOrMore(Pattern)}
     *
     * @return a {@code ZeroOrMore} pattern of {@code this}
     */
    public final ZeroOrMore<Value, Result> zeroOrMore() {
        return new ZeroOrMore<>(this);
    }

    /**
     * Convenience construction method for
     * {@link OneOrMore#OneOrMore(Pattern, Pattern.Greediness)}
     *
     * @param greediness  the greediness modifier for this pattern
     *
     * @throws NullPointerException
     *         if {@code greediness} is {@code null}
     *
     * @return a {@code OneOrMore} pattern of {@code this}
     */
    public final OneOrMore<Value, Result> oneOrMore(Greediness greediness) {
        return new OneOrMore<>(this, greediness);
    }

    /**
     * Convenience construction method for {@link OneOrMore#OneOrMore(Pattern)}
     *
     * @return a {@code OneOrMore} pattern of {@code this}
     */
    public final OneOrMore<Value, Result> oneOrMore() {
        return new OneOrMore<>(this);
    }

    /**
     * Convenience construction method for
     * {@link ZeroOrOne#ZeroOrOne(Pattern, Pattern.Greediness)}
     *
     * @param greediness  the greediness modifier for this pattern
     *
     * @throws NullPointerException
     *         if {@code greediness} is {@code null}
     *
     * @return a {@code ZeroOrOne} pattern of {@code this}
     */
    public final ZeroOrOne<Value, Result> zeroOrOne(Greediness greediness) {
        return new ZeroOrOne<>(this, greediness);
    }

    /**
     * Convenience construction method for {@link ZeroOrOne#ZeroOrOne(Pattern)}
     *
     * @return a {@code ZeroOrOne} pattern of {@code this}
     */
    public final ZeroOrOne<Value, Result> zeroOrOne() {
        return new ZeroOrOne<>(this);
    }

    /**
     * Convenience construction method for {@link Marked#Marked(Pattern)}
     *
     * @return a {@code Marked} pattern of {@code this}
     */
    public final Marked<Value, Result> marked() {
        return new Marked<>(this);
    }

    protected static <T> Collector<T, Set<T>, Set<T>> orderedSetCollector() {
        return Collector.of(LinkedHashSet::new,
                            Set::add,
                            (left, right) -> {
                                left.addAll(right);
                                return left;
                            });
    }
}
