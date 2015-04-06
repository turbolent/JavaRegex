package com.turbolent.regex.instructions;

import com.turbolent.regex.Parser;
import com.turbolent.regex.PartialMatch;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * The {@code Call} instruction invokes the given {@link #consumer consumer} with two arguments:
 * The {@linkplain com.turbolent.regex.Parser parser}; and the
 * {@linkplain PartialMatch partial match}, the match that was constructed
 * up until now in the current {@linkplain com.turbolent.regex.Thread thread}.
 * <p>
 * The {@code Call} instruction does <b>not</b> consume an input value
 * and the {@linkplain #next next instruction} is always executed.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Call<Value, Result> extends Instruction<Value, Result> {

    /**
     * The consumer to be invoked with the {@linkplain com.turbolent.regex.Parser parser}
     * and {@linkplain PartialMatch partial match}
     */
    public final BiConsumer<Parser, PartialMatch<Value, Result>> consumer;

    /**
     * Creates a {@code Call} instruction which invokes the specified {@code consumer}
     * with the {@linkplain com.turbolent.regex.Parser parser} and the
     * {@linkplain PartialMatch partial match}.
     *
     * @param consumer  the consumer to be invoked with the
     *                  {@linkplain com.turbolent.regex.Parser parser} and the
     *                  {@linkplain PartialMatch partial match}
     * @param next      the instruction to execute after the invocation of {@code consumer}
     *
     * @throws NullPointerException
     *         if {@code consumer} is {@code null}
     */
    public Call(BiConsumer<Parser, PartialMatch<Value, Result>> consumer,
                Instruction<Value, Result> next)
    {
        super(next);
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
    }
}
