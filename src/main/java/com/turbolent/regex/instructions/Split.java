package com.turbolent.regex.instructions;

/**
 * The {@code Split} instruction partitions the execution into two
 * {@linkplain com.turbolent.regex.Thread threads}, which will be executed simultaneously:
 * the first thread will execute instruction {@link #next next},
 * the second thread will execute instruction {@link #split split}.
 * <p>
 * The {@code Split} instruction does <b>not</b> consume an input value.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Split<Value, Result> extends Instruction<Value, Result> {

    /**
     * The next instruction to be executed in the second
     * {@linkplain com.turbolent.regex.Thread thread}
     */
    public Instruction<Value, Result> split;

    /**
     * Creates a {@code Split} instruction which partitions the execution into two
     * {@linkplain com.turbolent.regex.Thread threads}, which will be executed simultaneously
     *
     * @param next   the instruction to execute in the first thread
     * @param split  the instruction to execute in the second thread
     */
    public Split(Instruction<Value, Result> next, Instruction<Value, Result> split) {
        super(next);
        this.split = split;
    }
}
