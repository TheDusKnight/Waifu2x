package com.purdue.waifu2x.waifu2x;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent mIntent = getIntent();
        String path = mIntent.getStringExtra("Path");
        ImageView imageView = findViewById(R.id.imageView5);
        if (path != null) {
            imageView.setImageDrawable(Drawable.createFromPath(path));
        } else {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image, null));
        }
    }

    public void Back(View view) {
        finish();
    }
}
