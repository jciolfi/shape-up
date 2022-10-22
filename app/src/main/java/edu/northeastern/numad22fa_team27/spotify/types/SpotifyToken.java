package edu.northeastern.numad22fa_team27.spotify.types;

import java.sql.Timestamp;

public class SpotifyToken {
    public String accessToken;
    public long createdOn;
    public long expiresIn;

    public SpotifyToken(String accessToken, long createdOn, long expiresIn) {
        this.accessToken = accessToken;
        this.createdOn = createdOn;
        this.expiresIn = expiresIn;
    }

    boolean isExpired() {
        return new Timestamp(System.currentTimeMillis()).getTime() >= (createdOn + expiresIn);
    }
}
