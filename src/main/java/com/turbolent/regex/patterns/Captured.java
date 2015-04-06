package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Save;

import java.util.Objects;

import static com.turbolent.regex.instructions.Save.Position.END;
import static com.turbolent.regex.instructions.Save.Position.START;

/**
 * The {@code Captured} pattern matches the given {@link #pattern pattern}
 * and saves the matching values in the {@linkplain com.turbolent.regex.Match match} as a group,
 * using the given {@link #identifier identifier} as the name. The identifier may be {@code null}.
 * <p>
 * The matching values can be retrieved by passing the identifier to
 * {@link com.turbolent.regex.Match#group(Object)}.
 * <p>
 * The construction method {@link #captured(Object, Pattern)} is provided for convenience purposes.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Captured<Value, Result> extends Pattern<Value, Result> {
    private final Object identifier;
    private final Pattern<Value, Result> pattern;

    /**
     * Creates a {@code Captured} pattern which matches the given {@code pattern} and saves
     * the matching values in the {@linkplain com.turbolent.regex.Match match} as a group,
     * using the given {@code identifier} as the name
     *
     * @param identifier  the name of the group used when storing the values matching
     *                    {@code pattern} in the {@linkplain com.turbolent.regex.Match match}.
     *                    May be {@code null}
     * @param pattern     the pattern to be matched
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public Captured(Object identifier, Pattern<Value, Result> pattern) {
        Objects.requireNonNull(pattern);
        this.identifier = identifier;
        this.pattern = pattern;
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        final Instruction<Value, Result> end = new Save<>(this.identifier, END, next);
        final Instruction<Value, Result> code = this.pattern.compile(end);
        return new Save<>(this.identifier, START, code);
    }

    @Override
    public String toString() {
        return String.format("Captured(%s, %s)",
                             this.identifier, this.pattern);
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

        Captured<?, ?> captured = (Captured<?, ?>) other;
        return (Objects.equals(this.identifier, captured.identifier)
                && Objects.equals(this.pattern, captured.pattern));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier, this.pattern);
    }

    /**
     * Convenience construction method for {@link #Captured(java.lang.Object, Pattern)}
     *
     * @param identifier  the name of the group used when storing the values matching
     *                    {@code pattern} in the {@linkplain com.turbolent.regex.Match match}.
     *                    May be {@code null}
     * @param pattern     the pattern to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@code Captured} pattern matching {@code pattern}
     *
     * @throws NullPointerException
     *         if {@code pattern} is {@code null}
     */
    public static <Value, Result> Captured<Value, Result> captured
        (Object identifier, Pattern<Value, Result> pattern)
    {
        return new Captured<>(identifier, pattern);
    }
}
