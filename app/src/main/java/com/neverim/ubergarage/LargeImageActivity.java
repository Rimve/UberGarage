package com.neverim.ubergarage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LargeImageActivity extends AppCompatActivity {
    ImageView selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        selectedImage = findViewById(R.id.large_image_preview); // init a ImageView
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String path = extras.getString("path");
            if (path != null) {
                File imgFile = new File(path);
                Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                selectedImage.setImageBitmap(selectedBitmap);
                selectedImage.setClipToOutline(true);
            }
        } // get image from Intent and set it in ImageView
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
