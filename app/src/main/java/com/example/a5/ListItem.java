package com.example.a5;

import android.graphics.Bitmap;

public class ListItem {
    private int imageResource;
    private Bitmap imageBitmap;
    private String tagText;
    private boolean isChecked;
    private String imageType;

    public ListItem(int imageResourceId, String tagText, String imageType) {
        this.imageResource = imageResourceId;
        this.tagText = tagText;
        this.imageType = imageType;
        this.isChecked = false;
    }

    //using Bitmap instead of int works better for photos?
    public ListItem(Bitmap imageBitmap, String tagText) {
        this.imageBitmap = imageBitmap;
        this.tagText = tagText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getTagText() {
        return tagText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageType() {
        return imageType;
    }
}