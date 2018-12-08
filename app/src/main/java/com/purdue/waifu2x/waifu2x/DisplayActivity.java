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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent mIntent = getIntent();
        filePath = mIntent.getStringExtra("Path");
        ImageView imageView = findViewById(R.id.imageView5);
        imageView.setImageDrawable(Drawable.createFromPath(filePath));
    }

    public void Back(View view) {
        finish();
    }
}
