package com.turbolent.regex;

import com.turbolent.regex.instructions.Instruction;
import com.turbolent.regex.patterns.Captured;
import com.turbolent.regex.patterns.Char;
import com.turbolent.regex.patterns.Pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.turbolent.regex.patterns.Any.any;
import static com.turbolent.regex.patterns.Char.Char;
import static com.turbolent.regex.patterns.OneOfLiterals.oneOfLiterals;
import static com.turbolent.regex.patterns.OneOrMore.oneOrMore;
import static com.turbolent.regex.patterns.Pattern.Greediness.GREEDY;
import static com.turbolent.regex.patterns.Pattern.Greediness.LAZY;
import static com.turbolent.regex.patterns.ZeroOrMore.zeroOrMore;
import static com.turbolent.regex.patterns.ZeroOrOne.zeroOrOne;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class ParserTest {
    private static final double TIMEOUT = 0.5E10;

    private static class PatternAsserter<Value, Result> {
        private final Instruction<Value, Result> code;
        private final Pattern<Value, Result> pattern;

        private PatternAsserter(Pattern<Value, Result> pattern) {
            this.pattern = pattern;
            this.code = Pattern.compile(pattern);
        }

        private Match<Value, Result> match(Value[] values) {
            return Parser.match(this.code, asList(values));
        }

        @SafeVarargs
        private final PatternAsserter<Value, Result> assertSuccess(Value... values) {
            if (match(values) == null)
                throwAssertion("succeed", values);
            return this;
        }

        @SafeVarargs
        private final PatternAsserter<Value, Result> assertFailure(Value... values) {
            if (match(values) != null)
                throwAssertion("fail", values);
            return this;
        }

        private void throwAssertion(String description, Value[] values) {
            throw new AssertionError(String.format("Matching %s against %s should %s",
                                                   Arrays.deepToString(values),
                                                   this.pattern, description));
        }
    }

    @org.junit.Test
    public void testSimple() {
        new PatternAsserter<>(Char('a').then(Char('b')).then(Char('c')))
            .assertSuccess('a', 'b', 'c')
            .assertFailure('x', 'b', 'c')
            .assertFailure('a', 'x', 'c')
            .assertFailure('a', 'b', 'x');

        new PatternAsserter<>(Char('a').then(zeroOrMore(Char('b'))).then(Char('c')))
            .assertSuccess('a', 'b', 'c');

        new PatternAsserter<>(Char('a').then(zeroOrMore(Char('b')))
                                       .then(Char('b'))
                                       .then(Char('c')))
            .assertSuccess('a', 'b', 'c')
            .assertSuccess('a', 'b', 'b', 'c')
            .assertSuccess('a', 'b', 'b', 'b', 'b', 'c');

        new PatternAsserter<>(Char('a').then(oneOrMore(Char('b')))
                                       .then(Char('b'))
                                       .then(Char('c')))
            .assertSuccess('a', 'b', 'b', 'c')
            .assertFailure('a', 'b', 'c')
            .assertFailure('a', 'b', 'q')
            .assertSuccess('a', 'b', 'b', 'b', 'b', 'c');

        new PatternAsserter<>(Char('a').then(zeroOrOne(Char('b')))
                                       .then(Char('b'))
                                       .then(Char('c')))
            .assertSuccess('a', 'b', 'b', 'c')
            .assertSuccess('a', 'b', 'c')
            .assertFailure('a', 'b', 'b', 'b', 'b', 'c');

        new PatternAsserter<>(Char('a').then(zeroOrOne(Char('b'))).then(Char('c')))
            .assertSuccess('a', 'b', 'c');

        new PatternAsserter<>(Char('a').then(any()).then(Char('c')))
            .assertSuccess('a', 'b', 'c')
            .assertSuccess('a', 'x', 'c');

        new PatternAsserter<>(Char('a').then(zeroOrMore(any())).then(Char('c')))
            .assertSuccess('a', 'x', 'y', 'z', 'c')
            .assertFailure('a', 'x', 'y', 'z', 'd');

        new PatternAsserter<>(Char('a').then(oneOfLiterals('b', 'c')).then(Char('d')))
            .assertSuccess('a', 'b', 'd')
            .assertFailure('a', 'b', 'c');
    }

    @org.junit.Test
    public void testCapture() throws Exception {
        Captured<Character, Object> firstCapture =
            Char('a').then(Char('b')).capture("first");
        Captured<Character, Object> secondCapture =
            Char('c').then(Char('d')).capture("second");
        Pattern<Character, ?> pattern = Char('x')
            .then(firstCapture.or(secondCapture))
            .then(Char('y'))
            .capture(null);

        Instruction<Character, ?> code = Pattern.compile(pattern);

        List<Character> input1 = asList('x', 'c', 'd', 'y');
        Match<Character, ?> match1 = Parser.match(code, input1);
        assertNotNull(match1);
        assertEquals(input1, match1.group(null));
        assertEquals(null, match1.group("first"));
        assertEquals(asList('c', 'd'), match1.group("second"));

        List<Character> input2 = asList('x', 'a', 'b', 'y');
        Match<Character, ?> match2 = Parser.match(code, input2);
        assertNotNull(match2);
        assertEquals(input2, match2.group(null));
        assertEquals(null, match2.group("second"));
        assertEquals(asList('a', 'b'), match2.group("first"));

        List<Character> input3 = asList('f', 'o', 'o', 'b', 'a', 'r');
        Match<Character, ?> match3 = Parser.match(code, input3);
        assertNull(match3);
    }

    @org.junit.Test
    public void testStarGreediness() {
        List<Character> input = asList('<', 'a', '>', 'b', '<', '/', 'c', '>');

        Pattern<Character, ?> greedyPattern =
            Char('<').then(zeroOrMore(any(), GREEDY)).then(Char('>')).capture(null);
        Instruction<Character, ?> greedyCode = Pattern.compile(greedyPattern);
        Match<Character, ?> greedyMatch = Parser.match(greedyCode, input);
        assertNotNull(greedyMatch);
        assertEquals(input, greedyMatch.group(null));

        Pattern<Character, ?> nonGreedyPattern =
            Char('<').then(zeroOrMore(any(), LAZY)).then(Char('>')).capture(null);
        Instruction<Character, ?> nonGreedyCode = Pattern.compile(nonGreedyPattern);
        Match<Character, ?> nonGreedyMatch = Parser.match(nonGreedyCode, input);
        assertNotNull(nonGreedyMatch);
        assertEquals(asList('<', 'a', '>'), nonGreedyMatch.group(null));
    }

    @org.junit.Test
    public void testActionSimple() {
        AtomicInteger count = new AtomicInteger();
        Pattern<Character, ?> pattern = zeroOrOne(Char('a'))
            .call(match -> count.incrementAndGet());

        Instruction<Character, ?> code = Pattern.compile(pattern);
        List<Character> input = singletonList('a');
        Parser.match(code, input);
        assertEquals(2, count.get());
    }

    @org.junit.Test
    public void testActionComplex() {
        final AtomicBoolean pattern1ActionCalled = new AtomicBoolean();
        Pattern<Character, String> pattern1 =
            Char.<String>Char('a').capture("a").call(match -> {
                assertEquals(match.group("a"), singletonList('a'));
                assertNull(match.group("b"));
                assertNull(match.group("c"));
                assertNull(match.group("d"));
                match.setResult("A");
                pattern1ActionCalled.set(true);
            });

        final AtomicBoolean pattern2ActionCalled = new AtomicBoolean();
        Pattern<Character, String> pattern2 =
            Char.<String>Char('b').capture("b").call(match -> {
                assertEquals(match.group("a"), singletonList('a'));
                assertEquals(match.group("b"), singletonList('b'));
                assertNull(match.group("c"));
                assertNull(match.group("d"));
                match.setResult(match.getResult() + "B");
                pattern2ActionCalled.set(true);
            });

        final AtomicBoolean pattern3ActionCalled = new AtomicBoolean();
        Pattern<Character, String> pattern3 =
            Char.<String>Char('c').capture("c").call(match -> {
                assertEquals(match.group("a"), singletonList('a'));
                assertNull(match.group("b"));
                assertEquals(match.group("c"), singletonList('c'));
                assertNull(match.group("d"));
                match.setResult(match.getResult() + "C");
                pattern3ActionCalled.set(true);
            });

        final AtomicBoolean pattern4ActionCalled = new AtomicBoolean();
        Pattern<Character, String> pattern4 =
            Char.<String>Char('d').capture("d").call(match -> {
                assertEquals(match.group("a"), singletonList('a'));
                List<Character> bs = match.group("b");
                List<Character> cs = match.group("c");
                assertFalse(bs == null && cs == null);
                if (bs == null) {
                    assertEquals(cs, singletonList('c'));
                } else if (cs == null) {
                    assertEquals(bs, singletonList('b'));
                }
                assertEquals(match.group("d"), singletonList('d'));
                match.setResult(match.getResult() + "D");
                pattern4ActionCalled.set(true);
            });

        Pattern<Character, String> combinedPattern =
            pattern1.then(pattern2.or(pattern3)).then(pattern4);

        Instruction<Character, String> code2 = Pattern.compile(combinedPattern);
        List<Character> input2 = asList('a', 'c', 'd');
        Match<Character, String> match = Parser.match(code2, input2);

        assertTrue(pattern1ActionCalled.get());
        assertFalse(pattern2ActionCalled.get());
        assertTrue(pattern3ActionCalled.get());
        assertTrue(pattern4ActionCalled.get());
        assertNotNull(match);
        assertEquals(match.getResult(), "ACD");
    }

    @org.junit.Test
    public void testMarker() {
        List<Marker> markers = new ArrayList<>();
        Pattern<Character, Object> pattern =
            Char('a').then(zeroOrOne(Char('b')))
                     .call(match -> markers.add(match.getCurrentMarker()))
                     .marked();

        Pattern<Character, Object> concatenation = pattern.then(pattern);

        Instruction<Character, ?> code = Pattern.compile(concatenation);
        List<Character> input = asList('a', 'b', 'a', 'b');
        Parser.match(code, input);
        assertEquals(4, markers.size());
        assertEquals(markers.get(0), markers.get(1));
        assertNotEquals(markers.get(1), markers.get(2));
        assertEquals(markers.get(2), markers.get(3));
    }

    @org.junit.Test
    public void testOrder() {
        List<Character> input = asList('f', 'o', 'o');

        Pattern<Character, ?> pattern =
            Char('f').then(Char('o').or(zeroOrMore(Char('o')))).capture(null);
        Match<Character, ?> match = Parser.match(Pattern.compile(pattern), input);
        assertNotNull(match);
        assertEquals(asList('f', 'o'), match.group(null));

        Pattern<Character, ?> pattern2 =
            Char('f').then(zeroOrMore(Char('o')).or(Char('o'))).capture(null);
        Match<Character, ?> match2 = Parser.match(Pattern.compile(pattern2), input);
        assertNotNull(match2);
        assertEquals(asList('f', 'o', 'o'), match2.group(null));
    }

    @org.junit.Test
    public void testPolyTime() {
        final int N = 100;
        List<Character> input = Collections.nCopies(N, 'a');
        Pattern<Character, ?> pattern =
            zeroOrOne(Char('a')).repeat(N).then(Char('a').repeat(N));
        Instruction<Character, ?> code = Pattern.compile(pattern);
        long startTime = System.nanoTime();
        Match<Character, ?> match = Parser.match(code, input);
        double estimatedTime = System.nanoTime() - startTime;
        assertNotNull(match);
        assertThat(estimatedTime, lessThan(TIMEOUT));
    }
}