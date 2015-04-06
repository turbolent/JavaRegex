package com.turbolent.regex;

import com.turbolent.regex.instructions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A {@code Parser} matches a pattern, compiled into
 * {@linkplain com.turbolent.regex.instructions.Instruction matching instructions},
 * against a list of {@link #values values}.
 * <p>
 * An implementation of Rob Pike's Virtual Machine-based regular expression engine, as described
 * in great detail by Russ Cox in "Regular Expression Matching: the Virtual Machine Approach"
 * (see http://swtch.com/~rsc/regexp/regexp2.html)
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Parser<Value, Result> {
    private final HashSet<Instruction<Value, Result>> seen = new HashSet<>();
    final Instruction<Value, Result> code;
    final List<Value> values;

    private Parser(Instruction<Value, Result> code, List<Value> values) {
        this.code = code;
        this.values = values;
    }

    public static <Value, Result> Match<Value, Result> match(Instruction<Value, Result> code,
                                                             List<Value> values)
    {
        return new Parser<>(code, values).match();
    }

    private Match<Value, Result> match() {
        List<Thread<Value, Result>> currentThreads = new ArrayList<>();
        List<Thread<Value, Result>> newThreads = new ArrayList<>();

        ThreadState<Result> matchedState = null;

        int index = 0;
        addThread(new Thread<>(this.code, new ThreadState<>()),
                  index, currentThreads);
        for (; !currentThreads.isEmpty(); index++) {
            Value value = null;
            if (index < this.values.size()) {
                value = this.values.get(index);
            }

            this.seen.clear();
            for (int i = 0; i < currentThreads.size(); i++) {
                final Thread<Value, Result> thread = currentThreads.get(i);
                final Instruction<Value, Result> instruction = thread.instruction;
                final ThreadState<Result> state = thread.state;

                if (instruction instanceof Atom) {
                    Atom<Value, Result> atom = (Atom<Value, Result>)instruction;
                    if (value != null && atom.predicate.test(value))
                        addThread(new Thread<>(instruction.next, state),
                                  index + 1, newThreads);
                    else
                        state.decrementReferenceCount();
                } else if (instruction instanceof Accept) {
                    if (matchedState != null)
                        matchedState.decrementReferenceCount();

                    matchedState = state;

                    for (i++; i < currentThreads.size(); i++) {
                        Thread remainingThread = currentThreads.get(i);
                        remainingThread.state.decrementReferenceCount();
                    }

                    break;
                } else
                    throw Instruction.newUnsupportedException(instruction);
            }

            // swap currentThreads for newThreads
            List<Thread<Value, Result>> threads = currentThreads;
            currentThreads = newThreads;
            newThreads = threads;

            newThreads.clear();

            if (value == null)
                break;
        }

        if (matchedState == null)
            return null;
        return new Match<>(this.values, matchedState);
    }

    private void addThread(Thread<Value, Result> thread, int index,
                           List<Thread<Value, Result>> threads)
    {
        Instruction<Value, Result> instruction = thread.instruction;

        if (this.skipIfSeen(instruction)) {
            thread.state.decrementReferenceCount();
            return;
        }

        if (instruction instanceof Split) {
            final Split<Value, Result> split = (Split<Value, Result>)instruction;
            thread.state.incrementReferenceCount();
            addThread(new Thread<>(split.next, thread.state), index, threads);
            addThread(new Thread<>(split.split, thread.state), index, threads);
        } else if (instruction instanceof Save) {
            final Save<Value, Result> save = (Save<Value, Result>)instruction;
            final ThreadState<Result> state = thread.state.maybeCloneState();
            switch (save.position) {
                case START:
                    state.updateStartIndex(save.identifier, index);
                    break;
                case END:
                    state.updateEndIndex(save.identifier, index);
                    break;
                default:
                    throw Save.Position.newUnsupportedException(save.position);
            }
            addThread(new Thread<>(instruction.next, state), index, threads);
        } else if (instruction instanceof Mark) {
            final Mark<Value, Result> mark = (Mark<Value, Result>) instruction;
            final ThreadState<Result> state = thread.state.maybeCloneState();
            switch (mark.position) {
                case START:
                    state.addMark();
                    break;
                case END:
                    state.removeMark();
                    break;
                default:
                    throw Mark.Position.newUnsupportedException(mark.position);
            }
            addThread(new Thread<>(instruction.next, state), index, threads);
        } else if (instruction instanceof Call) {
            final Call<Value, Result> call = (Call<Value, Result>)instruction;
            final ThreadState<Result> state = thread.state.maybeCloneState();
            call.consumer.accept(this, new PartialMatch<>(this.values, state));
            addThread(new Thread<>(instruction.next, state), index, threads);
        } else
            threads.add(thread);
    }

    private boolean skipIfSeen(Instruction<Value, Result> instruction) {
        if (this.seen.contains(instruction)) {
            return true;
        } else {
            this.seen.add(instruction);
            return false;
        }
    }
}