package com.turbolent.regex.instructions;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The {@code Atom} instruction stops the current {@linkplain com.turbolent.regex.Thread thread}
 * if testing the {@link #predicate predicate} with the current input value fails.
 * Otherwise, the {@linkplain #next next instruction} is executed.
 * <p>
 * The {@code Atom} instruction consumes an input value and the next instruction
 * is only executed if testing the predicate with the current input succeeds.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Atom<Value, Result> extends Instruction<Value, Result> {

    /** The predicate used for testing the current input value */
    public final Predicate<Value> predicate;

    /**
     * Creates an {@code Atom} instruction that tests the current input value
     * with the specified {@code predicate}. The instruction {@code next} is executed
     * if the comparison succeeds.
     *
     * @param predicate  the predicate to use for testing the current input value
     * @param next       the instruction to execute if the test of the
     *                   current input value with {@code predicate} succeeds
     *
     * @throws NullPointerException
     *         if {@code predicate} is {@code null}
     */
    public Atom(Predicate<Value> predicate, Instruction<Value, Result> next) {
        super(next);
        Objects.requireNonNull(predicate);
        this.predicate = predicate;
    }
}
