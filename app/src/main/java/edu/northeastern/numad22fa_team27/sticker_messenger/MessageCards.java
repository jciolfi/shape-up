package edu.northeastern.numad22fa_team27.sticker_messenger;

import edu.northeastern.numad22fa_team27.sticker_messenger.models.StickerTypes;

public class MessageCards {
    private final StickerTypes sticker;
    private final String contact;
    private final String date;

    public MessageCards(StickerTypes sticker, String contact, String date) {
        this.sticker = sticker;
        this.contact = contact;
        this.date = date;
    }

    public StickerTypes getSticker() {
        return sticker;
    }

    public String getContact() {
        return contact;
    }

    public String getDate() {
        return date;
    }

}
