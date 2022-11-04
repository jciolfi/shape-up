package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.Date;

public class OutgoingMessage {
    // date sticker was sent
    public Date dateSent;
    // user the sticker will go to
    public String destUser;
    // unique identifier for a sticker
    public String sticker;
}
