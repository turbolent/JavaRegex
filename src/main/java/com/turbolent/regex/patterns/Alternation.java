package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.instructions.Split;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code Alternation} pattern matches one of a choice of {@link #patterns patterns}, in order.
 * It behaves like the logical OR operation.
 * <p>
 * Use {@link #alternation(Pattern, Pattern[])} to obtain an alternating pattern.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Alternation<Value, Result> extends Pattern<Value, Result> {

    /** The patterns to be matched */
    private final List<Pattern<Value, Result>> patterns;

    /**
     * Creates an {@code Alternation} pattern which matches
     * one of a choice of {@code patterns}, in order.
     *
     * @param patterns  the patterns to be matched
     *
     * @throws NullPointerException
     *         if {@code patterns} is {@code null}
     */
    private Alternation(List<Pattern<Value, Result>> patterns) {
        Objects.requireNonNull(patterns);
        this.patterns = patterns;
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        int size = this.patterns.size();
        if (size == 0)
            return next;

        final Pattern<Value, Result> pattern = this.patterns.get(0);
        Instruction<Value, Result> result = pattern.compile(next);
        if (size == 1)
            return result;

        final ListIterator<Pattern<Value, Result>> iterator =
            this.patterns.listIterator(1);
        while (iterator.hasNext()) {
            final Pattern<Value, Result> alternativePattern = iterator.next();
            result = new Split<>(result,
                                 alternativePattern.compile(next));
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Alternation(%s)",
                             this.patterns.stream()
                                          .map(Object::toString)
                                          .collect(Collectors.joining(", ")));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null
            || getClass() != object.getClass())
        {
            return false;
        }

        Alternation<?, ?> alternation = (Alternation<?, ?>) object;
        return Objects.equals(this.patterns, alternation.patterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.patterns);
    }

    private static <Value, Result> Stream<Pattern<Value, Result>> flatten
        (Pattern<Value, Result> pattern)
    {
        if (pattern instanceof Alternation) {
            final Alternation<Value, Result> alternation = (Alternation<Value, Result>) pattern;
            return alternation.patterns.stream()
                .flatMap(Alternation::<Value, Result>flatten);
        } else
            return Stream.of(pattern);
    }

    /**
     * Construction method for {@link #Alternation(java.util.List)}
     * which ensures there is at least one pattern to match
     *
     * @param pattern        the first pattern to be matched
     * @param otherPatterns  the other patterns to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return an {@link Alternation} pattern of {@code pattern} and the patterns in
     *         {@code otherPatterns}, if any; otherwise, just {@code pattern} itself
     *
     * @throws NullPointerException
     *         if {@code pattern} or any pattern in {@code otherPatterns} is null
     */
    @SafeVarargs
    public static <Value, Result> Pattern<Value, Result> alternation
        (Pattern<Value, Result> pattern,
         Pattern<Value, Result>... otherPatterns)
    {
        Objects.requireNonNull(pattern);

        if (otherPatterns.length == 0)
            return pattern;

        Set<Pattern<Value, Result>> patterns =
            Stream.concat(flatten(pattern),
                          Arrays.stream(otherPatterns)
                                .flatMap(Alternation::flatten))
                  .collect(orderedSetCollector());
        patterns.stream().forEach(Objects::requireNonNull);
        return new Alternation<>(new ArrayList<>(patterns));
    }
}
