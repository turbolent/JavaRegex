package com.turbolent.regex.instructions;

import java.util.Objects;

/**
 * The {@code Save} instruction stores the parsing operation's current input values index
 * in the {@linkplain com.turbolent.regex.Thread thread}'s
 * {@linkplain com.turbolent.regex.ThreadState state}, based on the given
 * {@linkplain #position position indicator}, using the given
 * {@link #identifier identifier} as the key.
 * <p>
 * A {@code Save} instruction with a {@linkplain Position#START START position indicator} should
 * eventually be followed by a {@code Save} instruction with
 * an {@linkplain Position#END END position indicator}.
 * <p>
 * The {@code Save} instruction does <b>not</b> consume an input value
 * and the {@linkplain #next next instruction} is always executed.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Save<Value, Result> extends Instruction<Value, Result> {

    /**
     * The position indicator for {@link Save} instructions
     *
     * @see Save
     */
    public enum Position {
        START,
        END;

        public static UnsupportedOperationException newUnsupportedException(Position position) {
            final String message = String.format("Unsupported position: %s", position);
            return new UnsupportedOperationException(message);
        }
    }

    /**
     * The key used when storing the input value index in the current
     * {@linkplain com.turbolent.regex.Thread thread}'s
     * {@linkplain com.turbolent.regex.ThreadState state}.
     * May be {@code null}
     */
    public final Object identifier;

    /**
     * The position indicator used when storing the input value index in the current
     * {@linkplain com.turbolent.regex.Thread thread}'s
     * {@linkplain com.turbolent.regex.ThreadState state}
     */
    public final Position position;

    /**
     * Creates a {@code Save} instruction which stores the current index (into the input values)
     * in the current {@linkplain com.turbolent.regex.Thread thread}'s
     * {@linkplain com.turbolent.regex.ThreadState state} using the given
     * {@code identifier} as the key, based on the given {@code position}.
     *
     * @param identifier  the key used when storing the input value index
     *                    in the current thread's state. May be {@code null}
     * @param position    the position indicator used when storing he input value index
     *                    in the current thread' state
     * @param next        the instruction to execute after saving
     *
     * @throws NullPointerException
     *         if {@code position} is {@code null}
     */
    public Save(Object identifier, Position position, Instruction<Value, Result> next) {
        super(next);
        Objects.requireNonNull(position);
        this.identifier = identifier;
        this.position = position;
    }

    @Override
    public String getArgument() {
        return String.format("%s: %s", this.position, this.identifier);
    }
}
