package edu.northeastern.numad22fa_team27.workout.converters;

public interface ModelConverter<A, B> {
    default A pack(B obj) { return null; }

    default B unpack(A obj) { return null; }
}
