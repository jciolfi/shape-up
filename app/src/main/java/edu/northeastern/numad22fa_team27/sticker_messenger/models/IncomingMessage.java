package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.Date;

public class IncomingMessage {
    // date sticker was sent
    private Date dateSent;
    // user that sent the sticker
    private String sourceUser;
    // unique identifier for sticker
    private StickerTypes sticker;

    public IncomingMessage() {}

    public IncomingMessage(OutgoingMessage msg, String sourceUser) {
        this.setDateSent(msg.getDateSent());
        this.setSourceUser(sourceUser);
        this.setSticker(msg.getSticker());
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(String sourceUser) {
        this.sourceUser = sourceUser;
    }

    public StickerTypes getSticker() {
        return sticker;
    }

    public void setSticker(StickerTypes sticker) {
        this.sticker = sticker;
    }
}
