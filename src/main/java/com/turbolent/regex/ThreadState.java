package com.turbolent.regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @param <Result>  the type of the match result
 */
public class ThreadState<Result> {
    private int referenceCount = 1;
    private final Map<Object, Integer> starts;
    private final Map<Object, Integer> ends;
    private final Deque<Marker> markers;
    private Result result = null;

    ThreadState() {
        this.starts = new HashMap<>();
        this.ends = new HashMap<>();
        this.markers = new ArrayDeque<>();
    }

    private ThreadState(Map<Object, Integer> starts, Map<Object, Integer> ends,
                        Deque<Marker> markers, Result result)
    {
        this.starts = new HashMap<>(starts);
        this.ends = new HashMap<>(ends);
        this.markers = new ArrayDeque<>(markers);
        this.result = result;
    }

    void updateStartIndex(Object key, int i) {
        this.starts.put(key, i);
    }

    void updateEndIndex(Object key, int i) {
        this.ends.put(key, i);
    }

    void addMark() {
        this.markers.addFirst(new Marker());
    }

    void removeMark() {
        this.markers.removeFirst();
    }

    Marker getCurrentMarker() {
        return this.markers.peekFirst();
    }

    ThreadState<Result> maybeCloneState() {
        if (this.referenceCount > 1) {
            this.referenceCount -= 1;
            return new ThreadState<>(this.starts, this.ends,
                                     this.markers, this.result);
        } else
            return this;
    }

    void incrementReferenceCount() {
        this.referenceCount += 1;
    }

    void decrementReferenceCount() {
        this.referenceCount -= 1;
    }

    Integer getStart(Object identifier) {
        return this.starts.get(identifier);
    }

    Integer getEnd(Object identifier) {
        return this.ends.get(identifier);
    }

    @Override
    public String toString() {
        return String.format("State(captures: {%s}, markers: %s, result: %s)",
                             this.ends.keySet().stream()
                                 .map(key -> String.format("%s: %d, %d", key,
                                                           this.starts.get(key),
                                                           this.ends.get(key)))
                                 .collect(Collectors.joining("; ")),
                             this.markers, this.result);
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }
}
