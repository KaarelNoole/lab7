package com.noole.lab7;

import android.net.Uri;

import androidx.annotation.NonNull;

public class Image {
    private final Uri uri;
    private final String name;
    private final int size;

    public Image(Uri uri, String name, int size) {
        this.uri = uri;
        this.name = name;
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }


    @NonNull
    @Override
    public String toString() {
        return this.name + " (" + (this.size / 1024) + "KB)";
    }
}
