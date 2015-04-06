package com.turbolent.regex.instructions;

import java.util.*;

/**
 * The root class in the <i>instruction hierarchy</i>.
 * An {@code Instruction} represents a single operation when matching input values.
 * It may or may <b>not</b> consume an input value,
 * and may prevent the execution of the {@linkplain #next next instruction}.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public abstract class Instruction<Value, Result> {
    /**
     * The instruction that should be executed after this instruction, if any
     * <p>
     * The field is not {@code final} to enable the construction of recursive instruction paths,
     * by initializing it to {@code null} and setting it to the actual succeeding instruction
     * later (e.g., one that is referring to this instruction)
     */
    public Instruction<Value, Result> next;

    /**
     * Creates an {@code Instruction} that executes the specified instruction {@code next}
     * after this instruction, if the current {@linkplain com.turbolent.regex.Thread thread}
     * has not been stopped yet.
     *
     * @param next  the instruction to execute after this instruction
     */
    protected Instruction(Instruction<Value, Result> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",
                             getName(),
                             getArgument());
    }

    // NOTE: Overridden as final to ensure not implemented in subclass:
    @SuppressWarnings("EmptyMethod")
    @Override
    public final boolean equals(Object other) {
        return super.equals(other);
    }

    // NOTE: Overridden as final to ensure not implemented in subclass
    @SuppressWarnings("EmptyMethod")
    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns the name of this instruction. Used in {@link #toString()}
     * and when {@linkplain #toDot() generating a DOT description}
     * of the graph rooted at this instruction.
     *
     * @return the string naming this instruction, by default
     *         the simple name of the instruction's class
     *
     * @see #toDot
     */
    protected String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Returns a description of the argument of this instruction.
     * Used in {@link #toString()} and when
     * {@linkplain #toDot() generating a DOT description}
     * of the graph rooted at this instruction.
     * <p>
     * Override this method to improve the detail of the output.
     *
     * @return the string describing the argument of this instruction,
     *         by default the empty string {@code ""}
     *
     * @see #toDot
     */
    protected String getArgument() {
        return "";
    }

    private int addDotEdge(Map<Instruction<Value, Result>, Integer> ids,
                           Instruction<Value, Result> instruction,
                           Deque<Instruction<Value, Result>> instructions,
                           StringBuilder builder,
                           int id, int nextId)
    {
        Integer succId = ids.get(instruction);
        if (succId == null) {
            succId = nextId++;
            ids.put(instruction, succId);
            instructions.addLast(instruction);
        }
        builder.append(String.format("i%d -> i%d%n", id, succId));
        return nextId;
    }

    /**
     * Generates a DOT description of the graph rooted at this instruction.
     * Provided for debugging purposes to visualize the instruction graph.
     *
     * @return the DOT description
     */
    public String toDot() {
        int nextId = 1;
        final Map<Instruction<Value, Result>, Integer> ids = new HashMap<>();

        final Deque<Instruction<Value, Result>> instructions =
            new ArrayDeque<>();
        ids.put(this, nextId++);
        instructions.add(this);

        final StringBuilder builder = new StringBuilder();

        builder.append("digraph code {\n");
        builder.append("rankdir = LR\n");
        builder.append("node [shape=box]\n");

        while (!instructions.isEmpty()) {
            final Instruction<Value, Result> instruction =
                instructions.removeFirst();
            final Integer id = ids.get(instruction);

            builder.append(String.format("i%d [label=\"%s %s\"%s]%n", id,
                                         instruction.getName(),
                                         instruction.getArgument(),
                                         ((instruction == this
                                           || instruction instanceof Accept)
                                          ? ", penwidth=2"
                                          : "")));

            if (instruction.next != null) {
                nextId = addDotEdge(ids, instruction.next, instructions,
                                    builder, id, nextId);
            }

            if (instruction instanceof Split) {
                final Split<Value, Result> split =
                    (Split<Value, Result>) instruction;
                nextId = addDotEdge(ids, split.split, instructions,
                                    builder, id, nextId);
            }
        }

        builder.append("}\n");

        return builder.toString();
    }

    public static <Value, Result> UnsupportedOperationException newUnsupportedException
        (Instruction<Value, Result> instruction)
    {
        final String message = String.format("Unsupported instruction: %s", instruction);
        return new UnsupportedOperationException(message);
    }

}
