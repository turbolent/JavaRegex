package com.turbolent.regex;

import java.util.List;

/**
 * A {@code Match} is the result of a successful
 * {@linkplain Parser#match(com.turbolent.regex.instructions.Instruction, java.util.List) parse}
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */

public class Match<Value, Result> {
    private final List<Value> input;
    protected final ThreadState<Result> state;

    Match(List<Value> input, ThreadState<Result> state) {
        this.input = input;
        this.state = state;
    }

    public List<Value> group(Object identifier) {
        Integer start = this.state.getStart(identifier);
        if (start == null)
            return null;
        Integer end = this.state.getEnd(identifier);
        return this.input.subList(start, end);
    }

    public Result getResult() {
        return this.state.getResult();
    }

    @Override
    public String toString() {
        return String.format("Match(%s)", this.state);
    }
}
