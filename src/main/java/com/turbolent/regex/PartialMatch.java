package com.turbolent.regex;

import java.util.List;

/**
 * A {@code PartialMatch} is a {@link Match} that was constructed up until
 * now in the current {@linkplain com.turbolent.regex.Thread thread}.
 * <p>
 * The {@link com.turbolent.regex.patterns.Call#consumer consumer} of a
 * {@linkplain com.turbolent.regex.patterns.Call Call pattern} has access to a partial match
 * and may use {@link #setResult(Object)} to update the current thread's result,
 * and {@link #getCurrentMarker()} to get the current {@link Marker},
 * if the {@linkplain com.turbolent.regex.patterns.Call Call pattern}
 * is part of a {@linkplain com.turbolent.regex.patterns.Marked Marked pattern}.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class PartialMatch<Value, Result> extends Match<Value, Result> {

    public PartialMatch(List<Value> input, ThreadState<Result> state) {
        super(input, state);
    }

    public void setResult(Result result) {
        this.state.setResult(result);
    }

    public Marker getCurrentMarker() {
        return this.state.getCurrentMarker();
    }
}
