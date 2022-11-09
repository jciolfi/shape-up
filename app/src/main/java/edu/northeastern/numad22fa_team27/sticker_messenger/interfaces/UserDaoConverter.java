package edu.northeastern.numad22fa_team27.sticker_messenger.interfaces;

public interface UserDaoConverter<A, B> {
    default B convert(A original) {
        return null;
    }
}
