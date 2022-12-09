package edu.northeastern.numad22fa_team27.workout.utilities;

import java.util.Objects;

// Needed because Firestore wants no-args constructors
public class StoreablePair<A, B> {
    A first;
    B second;

    public StoreablePair() {}

    public StoreablePair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreablePair<?, ?> that = (StoreablePair<?, ?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}
