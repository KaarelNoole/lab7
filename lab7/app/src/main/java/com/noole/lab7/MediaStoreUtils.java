package com.noole.lab7;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MediaStoreUtils {
    private static final String TAG;
    public static final MediaStoreUtils INSTANCE;

    public MediaStoreUtils(){}

    static {
        INSTANCE = new MediaStoreUtils();
        TAG = MediaStoreUtils.class.getName();
    }

    public final List<Image> loadImagesFromMediaStore(Context context){
        Uri externalStorageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id","_display_name","mime_type","_size"};
        Cursor cursor = context.getContentResolver().query(externalStorageURI,projection,null,null,null);
        List<Image> imageList = new ArrayList<>();
        if (cursor != null){
            try {
                int id_column = cursor.getColumnIndexOrThrow("_id");
                int name_column = cursor.getColumnIndexOrThrow("_display_name");
                int type_column = cursor.getColumnIndexOrThrow("_mime_type");
                int size_column = cursor.getColumnIndexOrThrow("_size");
                while (cursor.moveToNext()){
                    long id = cursor.getLong(id_column);
                    String name = cursor.getString(name_column);
                    String type = cursor.getString(type_column);
                    int size = cursor.getInt(size_column);
                    Log.i(TAG,"file: "+ id + " " + name + "\t\t--" + type + "\t\t-- (" + size + ")");
                    Uri uri = ContentUris.withAppendedId(externalStorageURI,id);
                    Image image = new Image(uri,name,size);
                    ((Collection<Image>)imageList).add(image);
                }
            }catch (Exception ex){
                Log.e(TAG,"loadImagesFromMedia: "+ex.getMessage());
            }
        }
        return imageList;
    }
}
