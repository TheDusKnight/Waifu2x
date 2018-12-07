package com.purdue.waifu2x.waifu2x;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class DisplayActivity extends AppCompatActivity {

    String filePath;
    String cachePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent mIntent = getIntent();
        cachePath = mIntent.getStringExtra("Path");
        ImageView imageView = findViewById(R.id.imageView5);
        if (cachePath != null) {
            imageView.setImageDrawable(Drawable.createFromPath(cachePath));
        } else {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image, null));
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + "/Waifu2x_Images/" + cachePath.substring(cachePath.lastIndexOf("Waifu")));
        filePath = file.getAbsolutePath();

        //Checking if file exists and displaying the download button if it doesn't
        if (file.exists()) {
            Toast.makeText(this, "This image has already been downloaded", Toast.LENGTH_SHORT).show();
        } else {
            Button download = findViewById(R.id.button8);
            download.setVisibility(View.VISIBLE);
        }
    }

    public void Back(View view) {
        finish();
    }

    public void Download(View view) {
        File file = new File(filePath);
        File cacheFile = new File(cachePath);
        final int chunkSize = 1024;
        byte[] imageData = new byte[chunkSize];
        try {
            InputStream in = getContentResolver().openInputStream(Uri.fromFile(cacheFile));
            OutputStream out = new FileOutputStream(file);
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.getStackTrace(); }

        Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show();
        //Disabling Download button after download
        view.setVisibility(View.GONE);
    }
}
