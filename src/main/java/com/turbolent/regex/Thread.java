package com.turbolent.regex;

import com.turbolent.regex.instructions.Instruction;

/**
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Thread<Value, Result> {
    final Instruction<Value, Result> instruction;
    final ThreadState<Result> state;

    Thread(Instruction<Value, Result> instruction, ThreadState<Result> state) {
        this.instruction = instruction;
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("Thread(%s, %s)",
                             this.instruction, this.state);
    }
}
