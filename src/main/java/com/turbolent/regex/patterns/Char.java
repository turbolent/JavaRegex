package com.turbolent.regex.patterns;

import com.turbolent.regex.instructions.Instruction;

import java.util.Objects;

/**
 * The {@code Char} pattern matches if the current input value
 * is the given {@link #character character}.
 * <p>
 * The construction method {@link #Char(char)} is provided for convenience purposes.
 *
 * @param <Result>  the type of the match result
 */
public class Char<Result> extends Pattern<Character, Result> {

    /** The character to compare with the current input value */
    private final char character;

    /**
     * Creates a {@code Char} pattern which matches if the current input value
     * is the given {@code character}.
     *
     * @param character  the character to compare with the current input value
     */
    public Char(char character) {
        this.character = character;
    }

    @Override
    protected Instruction<Character, Result> compile(Instruction<Character, Result> next) {
        return new com.turbolent.regex.instructions.Char<>(this.character, next);
    }

    @Override
    public String toString() {
        return String.format("Char(%c)", this.character);
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

        Char<?> otherChar = (Char<?>) other;
        return Objects.equals(this.character, otherChar.character);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.character);
    }

    /**
     * Convenience construction method for {@link #Char(char)}
     *
     * @param character  the character to compare with the current input value
     *
     * @param <Result>  the type of the match result
     *
     * @return a {@link Char} pattern which matches
     *         if the current input value is the given {@code character}
     */
    public static <Result> Char<Result> Char(char character) {
        return new Char<>(character);
    }
}