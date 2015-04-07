package com.turbolent.regex.patterns;

import com.turbolent.regex.Parser;
import com.turbolent.regex.PartialMatch;
import com.turbolent.regex.instructions.Instruction;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The {@code Call} pattern matches the given {@link #pattern pattern} and invokes the given
 * {@link #consumer consumer} with two arguments:
 * The {@linkplain com.turbolent.regex.Parser parser},
 * and the {@linkplain PartialMatch partial match}, the match that was constructed
 * up until now in the current {@linkplain com.turbolent.regex.Thread thread}.
 * The given {@link #moment moment} indicates when the consumer is to be invoked,
 * relative to the matching of the {@link #pattern pattern}.
 * <p>
 * The construction methods {@link #call(java.util.function.BiConsumer, Pattern)} and
 * {@link #call(java.util.function.Consumer, Pattern)} are provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Call<Value, Result> extends Pattern<Value, Result> {

    /**
     * The moment for {@link Call} patterns indicates when the consumer is to be invoked,
     * relative to the matching of its {@link Call#pattern pattern}.
     */
    public enum Moment {
        /** indicates the consumer is to be invoked before matching the pattern */
        BEFORE,
        /** indicates the consumer is to be invoked after matching the pattern */
        AFTER;

        private static UnsupportedOperationException newUnsupportedException(Moment moment) {
            final String message = String.format("Unsupported moment: %s", moment);
            return new UnsupportedOperationException(message);
        }
    }

    /** The moment the {@link #consumer consumer} is invoked by default */
    public static final Moment DEFAULT_MOMENT = Moment.AFTER;

    /**
     * The consumer to be invoked with the {@linkplain com.turbolent.regex.Parser parser}
     * and the {@linkplain PartialMatch partial match}
     */
    private final BiConsumer<Parser, PartialMatch<Value, Result>> consumer;

    /** The pattern to be matched */
    private final Pattern<Value, Result> pattern;

    /** The moment when the {@code consumer} is to be invoked, relative to the {@link #pattern} */
    private final Moment moment;

    /**
     * Creates a {@code Call} pattern which matches the given {@code pattern} and
     * invokes the given {@code consumer} with the
     * {@linkplain com.turbolent.regex.Parser parser} and
     * the {@linkplain PartialMatch partial match}.
     * <p>
     * The {@code moment} indicates when the consumer is to be invoked, relative to
     * the matching of the given {@code pattern}.
     *
     * @param consumer  the consumer to be invoked with the
     *                  {@linkplain com.turbolent.regex.Parser parser} and the
     *                  {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     * @param moment    the moment when the {@code consumer} is to be invoked, relative to
     *                  the {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code consumer}, {@code pattern} or {@code moment} is {@code null}
     */
    public Call(BiConsumer<Parser, PartialMatch<Value, Result>> consumer,
                Pattern<Value, Result> pattern, Moment moment)
    {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(moment);
        this.consumer = consumer;
        this.pattern = pattern;
        this.moment = moment;
    }

    /**
     * Creates a {@code Call} pattern which matches the given {@code pattern}
     * and invokes the given {@code consumer} with the
     * {@linkplain com.turbolent.regex.Parser parser}
     * and the {@linkplain PartialMatch partial match}.
     * <p>
     * The moment when the consumer is to be invoked is {@link #DEFAULT_MOMENT}.
     *
     * @param consumer  the consumer to be invoked with the
     *                  {@linkplain com.turbolent.regex.Parser parser} and the
     *                  {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code consumer} or {@code pattern} is {@code null}
     */
    public Call(BiConsumer<Parser, PartialMatch<Value, Result>> consumer,
                Pattern<Value, Result> pattern)
    {
        this(consumer, pattern, DEFAULT_MOMENT);
    }

    /**
     * Creates a {@code Call} pattern which matches the given {@code pattern} and
     * invokes the given {@code consumer} with the {@linkplain PartialMatch partial match}.
     * <p>
     * The {@code moment} indicates when the consumer is to be invoked, relative to
     * the matching of the given {@code pattern}.
     *
     * @param consumer  the consumer to be invoked with the {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     * @param moment    the moment when the {@code consumer} is to be invoked, relative to
     *                  the {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code consumer}, {@code pattern} or {@code moment} is {@code null}
     */
    public Call(Consumer<PartialMatch<Value, Result>> consumer,
                Pattern<Value, Result> pattern, Moment moment)
    {
        this((parser, partialMatch) -> consumer.accept(partialMatch),
             pattern, moment);
        Objects.requireNonNull(consumer);
    }

    /**
     * Creates a {@code Call} pattern which matches the given {@code pattern} and
     * invokes the given {@code consumer} with the {@linkplain PartialMatch partial match}.
     * <p>
     * The moment when the consumer is to be invoked is {@link #DEFAULT_MOMENT}.
     *
     * @param consumer  the consumer to be invoked with the {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code consumer} or {@code pattern} is {@code null}
     */
    public Call(Consumer<PartialMatch<Value, Result>> consumer,
                Pattern<Value, Result> pattern)
    {
        this(consumer, pattern, DEFAULT_MOMENT);
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        switch (this.moment) {
            case BEFORE: {
                final Instruction<Value, Result> code = this.pattern.compile(next);
                return new com.turbolent.regex.instructions.Call<>(this.consumer, code);
            }
            case AFTER: {
                final Instruction<Value, Result> call =
                    new com.turbolent.regex.instructions.Call<>(this.consumer, next);
                return this.pattern.compile(call);
            }
            default:
                throw Moment.newUnsupportedException(this.moment);
        }
    }

    private Object[] getToStringArguments() {
        switch (this.moment) {
            case BEFORE:
                return new Object[]{ this.consumer, this.pattern };
            case AFTER:
                return new Object[]{ this.pattern, this.consumer };
            default:
                throw Moment.newUnsupportedException(this.moment);
        }
    }

    @Override
    public String toString() {
        return String.format("Call(%s, %s)",
                             (Object[])getToStringArguments());
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

        final Call<?, ?> call = (Call<?, ?>) other;
        return (Objects.equals(this.consumer, call.consumer)
                && Objects.equals(this.pattern, call.pattern)
                && Objects.equals(this.moment, call.moment));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.consumer, this.pattern, this.moment);
    }

    /**
     * Convenience construction method for
     * {@link #Call(java.util.function.BiConsumer, Pattern)}
     *
     * @param consumer  the consumer to be invoked with the
     *                  {@linkplain com.turbolent.regex.Parser parser} and the
     *                  {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     **
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {Call} pattern matching {@code pattern} and invoking {@code consumer}
     *
     * @throws NullPointerException
     *         if {@code consumer} or {@code pattern} is {@code null}
     */
    public static <Value, Result> Call<Value, Result> call
        (BiConsumer<Parser, PartialMatch<Value, Result>> consumer, Pattern<Value, Result> pattern)
    {
        return new Call<>(consumer, pattern);
    }

    /**
     * Convenience construction method for
     * {@link #Call(java.util.function.Consumer, Pattern)}
     * @param consumer  the consumer to be invoked with the {@linkplain PartialMatch partial match}
     * @param pattern   the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Call} pattern matching {@code pattern} and invoking {@code consumer}
     *
     * @throws NullPointerException
     *         if {@code consumer} or {@code pattern} is {@code null}
     */
    public static <Value, Result> Call<Value, Result> call
        (Consumer<PartialMatch<Value, Result>> consumer, Pattern<Value, Result> pattern)
    {
        return new Call<>(consumer, pattern);
    }
}
