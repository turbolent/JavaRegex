package com.turbolent.regex;

/**
 * A {@code Marker} is a unique identifier valid while matching
 * a {@linkplain com.turbolent.regex.patterns.Marked marked pattern}.
 */
public class Marker {
    @Override
    public String toString() {
        return String.format("Marker(%s)",
                             System.identityHashCode(this));
    }
}
