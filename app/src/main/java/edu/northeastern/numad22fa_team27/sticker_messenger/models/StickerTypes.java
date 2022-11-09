package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import android.util.Pair;

import edu.northeastern.numad22fa_team27.R;

public enum StickerTypes {
    STICKER_1(R.drawable.arcade_vectorportal),
    STICKER_2 (R.drawable.baseball_vectorportal),
    STICKER_3(R.drawable.vinyl_vectorportal),
    STICKER_4(R.drawable.chicken_bucket_vectorportal),
    STICKER_5(R.drawable.cassette_vectorportal);

    public final int imgId;

    StickerTypes(int imgId) {
        this.imgId = imgId;
    }

    public static String getNameFromEnum(StickerTypes sticker) {
        switch (sticker) {
            case STICKER_1:
                return "Arcade";
            case STICKER_2:
                return "Baseball";
            case STICKER_3:
                return "Vinyl";
            case STICKER_4:
                return "Chicken Bucket";
            case STICKER_5:
                return "Cassette";
            default:
                return "Unknown";
        }
    }

}
