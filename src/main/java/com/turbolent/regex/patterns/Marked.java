package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Mark;

import java.util.Objects;

import static com.turbolent.regex.instructions.Mark.Position.END;
import static com.turbolent.regex.instructions.Mark.Position.START;

/**
 * The {@code Marked} pattern matches the given {@link #pattern pattern}.
 * While matching the pattern, calling {@link com.turbolent.regex.PartialMatch#getCurrentMarker()}
 * will return a unique {@link com.turbolent.regex.Marker}. {@code Marked} patterns may be nested.
 * <p>
 * The {@linkplain com.turbolent.regex.PartialMatch partial match}
 * can be accessed through a {@link Call} pattern.
 * <p>
 * The construction method {@link #marked(Pattern)} is provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */

public class Marked<Value, Result> extends Pattern<Value, Result> {
    private final Pattern<Value, Result> pattern;

    /**
     * Creates a {@code Marked} pattern which matches the given {@code pattern}.
     * While matching the pattern, calling
     * {@link com.turbolent.regex.PartialMatch#getCurrentMarker()} will return a unique
     * {@link com.turbolent.regex.Marker}.
     * <p>
     * {@code pattern} may be or contain another {@code Marked} pattern.
     *
     * @param pattern  the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public Marked(Pattern<Value, Result> pattern) {
        Objects.requireNonNull(pattern);
        this.pattern = pattern;
    }

    @Override
    protected Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        final Instruction<Value, Result> end = new Mark<>(END, next);
        final Instruction<Value, Result> code = this.pattern.compile(end);
        return new Mark<>(START, code);
    }

    @Override
    public String toString() {
        return String.format("Marked(%s)", this.pattern);
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

        Marked<?, ?> marked = (Marked<?, ?>) other;
        return Objects.equals(this.pattern, marked.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern);
    }

    /**
     * Convenience construction method for {@link #Marked(Pattern)}
     *
     * @param pattern  the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Marked} pattern which matches the given {@code pattern}
     */
    public static <Value, Result> Marked<Value, Result> marked(Pattern<Value, Result> pattern) {
        return new Marked<>(pattern);
    }
}
