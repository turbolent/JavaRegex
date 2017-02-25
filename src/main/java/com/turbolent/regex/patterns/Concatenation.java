package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code Concatenation} pattern matches the given {@link #patterns patterns} in order.
 * <p>
 * Use {@link #concatenation(Pattern, Pattern[])} to obtain a {@code Concatenation} pattern.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Concatenation<Value, Result> extends Pattern<Value, Result> {

    /** The patterns to be matched */
    private final List<Pattern<Value, Result>> patterns;

    /**
     * Creates a {@code Concatenation} pattern which matches the given {@code patterns} in order.
     *
     * @param patterns  the patterns to be matched
     *
     * @throws NullPointerException
     *         if {@code patterns} is {@code null}
     */
    private Concatenation(List<Pattern<Value, Result>> patterns) {
        Objects.requireNonNull(patterns);
        this.patterns = patterns;
    }

    @Override
    public Instruction<Value, Result> compile(Instruction<Value, Result> next) {
        Instruction<Value, Result> result = next;
        final ListIterator<Pattern<Value, Result>> iterator =
            this.patterns.listIterator(this.patterns.size());
        while (iterator.hasPrevious()) {
            final Pattern<Value, Result> pattern = iterator.previous();
            result = pattern.compile(result);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Concatenation(%s)",
                             this.patterns.stream()
                                 .map(Object::toString)
                                 .collect(Collectors.joining(", ")));
    }
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null
            || getClass() != other.getClass())
        {
            return false;
        }

        Concatenation<?, ?> concatenation = (Concatenation<?, ?>) other;
        return Objects.equals(this.patterns, concatenation.patterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.patterns);
    }

    private static <Value, Result> Stream<Pattern<Value, Result>> flatten
        (Pattern<Value, Result> pattern)
    {
        if (pattern instanceof Concatenation) {
            final Concatenation<Value, Result> concatenation =
                (Concatenation<Value, Result>)pattern;
            return concatenation.patterns.stream()
                .flatMap(Concatenation::<Value, Result>flatten);
        } else
            return Stream.of(pattern);
    }

    /**
     * Construction method for {@link #Concatenation(java.util.List)}
     * which ensures there is at least one pattern to match
     *
     * @param pattern        the first pattern to be matched
     * @param otherPatterns  the other patterns to be matched
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return a {@link Concatenation} pattern of {@code pattern} and the patterns in
     *         {@code otherPatterns}, if any; otherwise, just {@code pattern} itself
     *
     * @throws NullPointerException
     *         if {@code pattern} or any pattern in {@code otherPatterns} is null
     */
    @SafeVarargs
    public static <Value, Result> Pattern<Value, Result> concatenation
        (Pattern<Value, Result> pattern,
         Pattern<Value, Result>... otherPatterns)
    {
        Objects.requireNonNull(pattern);
        if (otherPatterns.length == 0)
            return pattern;

        final List<Pattern<Value, Result>> patterns =
            Stream.concat(flatten(pattern),
                          Arrays.stream(otherPatterns)
                                .flatMap(Concatenation::flatten))
                  .collect(Collectors.toList());
        patterns.stream().forEach(Objects::requireNonNull);
        return new Concatenation<>(patterns);
    }
}
