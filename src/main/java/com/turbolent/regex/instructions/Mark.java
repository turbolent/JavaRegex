package com.turbolent.regex.instructions;

import java.util.Objects;

/**
 * The {@code Mark} instruction modifies the stack of
 * {@linkplain com.turbolent.regex.Marker markers} of the current
 * {@linkplain com.turbolent.regex.Thread thread}'s
 * {@linkplain com.turbolent.regex.ThreadState state}.
 * <p>
 * A {@code Mark} instruction with the {@linkplain Position#START START position indicator}
 * generates a new marker and adds it to the current thread's state.
 * A {@code Mark} instruction with the {@linkplain Position#END END position indicator} removes
 * the latest marker from the current thread's state.
 * <p>
 * A {@code Mark} instruction with a {@linkplain Position#START START position indicator} should
 * eventually be followed by a {@code Mark} instruction with an
 * {@linkplain Position#END END position indicator}.
 * <p>
 * The Mark instruction does <b>not</b> consume an input value
 * and the {@linkplain #next next instruction} is always executed.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Mark<Value, Result> extends Instruction<Value, Result> {

    /**
     * The position indicator for {@link Mark} instructions
     *
     * @see Mark
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
     * The position indicator used when modifying the stack of
     * {@linkplain com.turbolent.regex.Marker markers}
     * of the current {@linkplain com.turbolent.regex.Thread thread}'s
     * {@linkplain com.turbolent.regex.ThreadState state}
     */
    public final Position position;

    /**
     * Creates a {@code Mark} instruction which modifies the stack of
     * {@linkplain com.turbolent.regex.Marker markers}
     * of the current {@linkplain com.turbolent.regex.Thread thread}'s
     * {@linkplain com.turbolent.regex.ThreadState state}, based on the given {@code position}.
     *
     * @param position    the position indicator used when modifying the stack of markers
     *                    of the current thread's state
     * @param next        the instruction to execute after performing the marker operation
     *
     * @throws NullPointerException
     *         if {@code position} is {@code null}
     */
    public Mark(Position position, Instruction<Value, Result> next) {
        super(next);
        Objects.requireNonNull(position);
        this.position = position;
    }

    @Override
    public String getArgument() {
        return String.valueOf(this.position);
    }
}

