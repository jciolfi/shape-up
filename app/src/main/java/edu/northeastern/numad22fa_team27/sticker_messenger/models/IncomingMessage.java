package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.Date;

public class IncomingMessage {
    // date sticker was sent
    public Date dateSent;
    // user that sent the sticker
    public String sourceUser;
    // unique identifier for sticker
    public String sticker;
}
