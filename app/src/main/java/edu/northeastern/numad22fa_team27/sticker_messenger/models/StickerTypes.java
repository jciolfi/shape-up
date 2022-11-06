package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import edu.northeastern.numad22fa_team27.R;

public enum StickerTypes {
    STICKER_1(R.drawable.arcade_vectorportal),
    STICKER_2 (R.drawable.baseball_vectorportal),
    STICKER_3(R.drawable.cassette_vectorportal),
    STICKER_4(R.drawable.chicken_bucket_vectorportal),
    STICKER_5(R.drawable.cassette_vectorportal);

    public final int imgId;

    StickerTypes(int imgId) {
        this.imgId = imgId;
    }

}
