package com.noole.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private  static final String TAG = MainActivity.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Spinner spinner;
    private ImageView chosenImage;
    MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        spinner = findViewById(R.id.spinner);
        chosenImage = findViewById(R.id.imageView);
        Button mainThread = findViewById(R.id.button);
        Button workerThread = findViewById(R.id.button2);


        mainThread.setOnClickListener(view -> workOnMainThread());
        workerThread.setOnClickListener(view -> workOnWorkerThread());
        setUpObserver();

        setUpFileSpinner();
    }

    private void setUpObserver() {
        Observer chosenImageObserver = new Observer() {
            @Override
            public void onChanged(Object obj) {
                this.onChanged((Bitmap)obj);
            }
            public final void onChanged(Bitmap newImage){
                chosenImage.setImageBitmap(newImage);
            }
        };
        mainViewModel.getSelectedImage().observe(this,chosenImageObserver);
    }

    private void workOnWorkerThread() {
        Image selectedImage = (Image) spinner.getSelectedItem();
        mainViewModel.imageChosenThreaded(selectedImage);
        /*
        Uri uri = selectedImage.getUri();
        new  Thread(() -> {
            Bitmap bitmap = loadImage(uri);
            double ratio = (double)bitmap.getWidth() / (double)bitmap.getHeight();
            Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap,(int)((double)800*ratio),800,false );
            runOnUiThread(() -> chosenImage.setImageBitmap(bitmap));
        }).start();

         */
    }

    private void workOnMainThread() {
        Log.i(TAG, "Loading image on main thread");
        Image selectedImage = (Image) spinner.getSelectedItem();
        mainViewModel.imageChosen(selectedImage);
        /*
        Image selectedImage = (Image) spinner.getSelectedItem();
        Uri uri = selectedImage.getUri();
        Bitmap bitmap = loadImage(uri);
        chosenImage.setImageBitmap(bitmap);

         */
    }

    private Bitmap loadImage(Uri uri) {
        Bitmap bitmap = null;
        InputStream stream;
        try {
            stream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void setUpFileSpinner() {
        if (checkStoragePermission()){
            Toast.makeText(this,"External permission granted!", Toast.LENGTH_SHORT).show();
            List<Image> imageList = MediaStoreUtils.INSTANCE.loadImagesFromMediaStore(this);
            ArrayAdapter<Image> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,imageList);
            spinner.setAdapter(adapter);
        }else requestStoragePermission();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE },PERMISSION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "External permission granted!", Toast.LENGTH_SHORT).show();
                setUpFileSpinner();
            }else {
                Log.w(TAG, "App cant run without external storage permission!");
            }
        }
    }
}