package com.turbolent.regex.instructions;

/**
 * The {@code Accept} instruction stops the current {@linkplain com.turbolent.regex.Thread thread}
 * and indicates the matching of all input values succeeded.
 * <p>
 * Use {@link #accept()} to obtain an {@code Accept} instruction.
 */
public final class Accept<Value, Result> extends Instruction<Value, Result> {

    /**
     * The {@code Accept} instruction
     *
     * @see #accept()
     */
    private static final Accept ACCEPT = new Accept();

    /**
     * Creates an {@code Accept} instruction which stops the current
     * {@linkplain com.turbolent.regex.Thread thread} and
     * indicates the matching of all input values succeeded.
     */
    private Accept() {
        super(null);
    }

    /**
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return an {@code Accept} instruction
     *
     * @see #ACCEPT
     */
    @SuppressWarnings("unchecked")
    public static <Value, Result> Accept<Value, Result> accept() {
        return (Accept<Value, Result>) ACCEPT;
    }
}
