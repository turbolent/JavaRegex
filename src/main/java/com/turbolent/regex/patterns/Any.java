package com.turbolent.regex.patterns;

/**
 * The {@code Any} pattern matches any input value.
 * <p>
 * Use {@link #any()} to obtain a properly typed instance of an {@code Any} pattern.
 *
 * @param <Value>   the type of the input values
 * @param <Result>  the type of the match result
 */
public class Any<Value, Result> extends Test<Value, Result> {

    /**
     * The {@code Any} pattern.
     *
     * @see #any()
     */
    private static final Any ANY = new Any();

    /**
     * Creates an {@code Any} pattern, which consumes any input value.
     */
    @SuppressWarnings("unchecked")
    private Any() {
        super(x -> true);
    }

    /**
     *
     * @param <Value>   the type of the input values
     * @param <Result>  the type of the match result
     *
     * @return an {@code Any} pattern which matches any input value
     *
     * @see #ANY
     */
    @SuppressWarnings("unchecked")
    public static <Value, Result> Any<Value, Result> any() {
        return (Any<Value, Result>) ANY;
    }

    @Override
    public String toString() {
        return "Any";
    }
}
