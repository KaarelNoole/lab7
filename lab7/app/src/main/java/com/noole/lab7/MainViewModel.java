package com.noole.lab7;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getName();
    private final MutableLiveData<Bitmap> selectedImage;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.selectedImage = new MutableLiveData<>();

    }

    public MutableLiveData<Bitmap> getSelectedImage() {
        return selectedImage;
    }

    public Bitmap loadImage(Image image) {
        Bitmap bitmap = null;
        InputStream stream;
        try {
            stream = getApplication().getContentResolver().openInputStream(image.getUri());
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public final void imageChosenThreaded(Image image){
        new  Thread(() -> {
            Log.i(TAG, "run: happening on new thread");
            Bitmap bitmap =MainViewModel.this.loadImage(image);
            double ratio = (double)bitmap.getWidth() / (double)bitmap.getHeight();
            Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap,(int)((double)800*ratio),800,false );
            MainViewModel.this.getSelectedImage().postValue(scaledImage);
        }).start();
    }

    public final void imageChosen(Image image){
        Bitmap bmp = this.loadImage(image);
        this.selectedImage.setValue(bmp);
    }


}
