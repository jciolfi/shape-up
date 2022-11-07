package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.io.Serializable;
import java.util.Date;

public class OutgoingMessage implements Serializable {
    // date sticker was sent
    private Date dateSent;
    // user the sticker will go to
    private String destUser;
    // unique identifier for a sticker
    private StickerTypes sticker;

    public OutgoingMessage() {}

    public OutgoingMessage(Date dateSent, String destUser, StickerTypes sticker) {
        this.setDateSent(dateSent);
        this.setDestUser(destUser);
        this.setSticker(sticker);
    }

    public OutgoingMessage(IncomingMessage msg, String destUser) {
        this.setDateSent(msg.getDateSent());
        this.setDestUser(destUser);
        this.setSticker(msg.getSticker());
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getDestUser() {
        return destUser;
    }

    public void setDestUser(String destUser) {
        this.destUser = destUser;
    }

    public StickerTypes getSticker() {
        return sticker;
    }

    public void setSticker(StickerTypes sticker) {
        this.sticker = sticker;
    }
}
