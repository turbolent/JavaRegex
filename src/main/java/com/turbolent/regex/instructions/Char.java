package com.turbolent.regex.instructions;

/**
 * The {@code Char} instruction stops the current {@linkplain com.turbolent.regex.Thread thread}
 * if the current input value is not the given {@link #character character}. Otherwise,
 * the {@linkplain #next next instruction} is executed.
 * <p>
 * The {@code Char} instruction is an {@link Atom} instruction specialized for
 * {@linkplain java.lang.Character characters}. It is provided for convenience.
 * <p>
 * The {@code Char} instruction consumes an input value and the {@linkplain #next next instruction}
 * is only executed if the current input value is the given character.
 *
 * @param <Result>  the type of the match result
 */
public final class Char<Result> extends Atom<Character, Result> {

    /** The character used for testing the current input character */
    private final char character;

    /**
     * Creates a {@code Char} instruction that tests
     * if the current input value is the given {@code character}.
     *
     * @param character  the character to use for the comparison with the current input value
     * @param next       the instruction to execute if the comparison succeeds
     */
    public Char(char character, Instruction<Character, Result> next) {
        super(c -> c == character, next);
        this.character = character;
    }

    @Override
    public String getArgument() {
        return String.valueOf(this.character);
    }
}
