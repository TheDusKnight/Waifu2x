package com.purdue.waifu2x.waifu2x;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main2Activity extends AppCompatActivity {

    String filePath;
    String cachePath;
    boolean flip = true;
    ImageView image_new;
    ImageView image_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        image_new = findViewById(R.id.imageView);
        image_old = findViewById(R.id.imageView_old);

        Intent mIntent = getIntent();
        String uriString = mIntent.getStringExtra("Image");
        Uri getImage = Uri.parse(uriString);

        try {
            InputStream is = getContentResolver().openInputStream(getImage);
            image_old.setImageDrawable(Drawable.createFromStream(is, getImage.toString()));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Oops, something went wrong when loading images",
                    Toast.LENGTH_LONG).show();
        }

        //still need converted image for image_new

        //Letting user swipe to change between images
        FrameLayout myLayout = findViewById(R.id.frame);

        final GestureDetector gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i("APP_TAG", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 100;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 100;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if ((e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                            || e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE)
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        switchImages();
                    }
                } catch (Exception e) {
                    // nothing
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });
        myLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent (event);
            }
        });
    }

    //    public void compare(View view) {
//        Intent mIntent=new Intent(this, MainActivity.class);
//        startActivity(mIntent);
//    }
    public void compare(View view) {
        switchImages();
    }

    public void switchImages() {
        if (flip) {
            image_old.setVisibility(View.VISIBLE);
            image_new.setVisibility(View.GONE);
            Toast.makeText(this, "Now Viewing: Original Image", Toast.LENGTH_SHORT).show();
        } else {
            image_old.setVisibility(View.GONE);
            image_new.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Now Viewing: Modified Image", Toast.LENGTH_SHORT).show();
        }
        flip = !flip;
    }

    public void share(View view) {
        Intent shareImage = new Intent(Intent.ACTION_SEND);
        shareImage.setType("image/*");
        Uri imageUri = Uri.parse("file://" + filePath);
        shareImage.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareImage, "Share via"));
    }

    public void download(View view) {

    }

    public void undo(View view) {
    }

    public void Start(View view) {
        Intent mIntent=new Intent(this, MainActivity.class);
        startActivity(mIntent);
    }

    public void Library(View view) {
        Intent mIntent=new Intent(this, Main3Activity.class);
        startActivity(mIntent);
    }

    public void About(View view) {
        Intent mIntent=new Intent(this, Main4Activity.class);
        startActivity(mIntent);
    }
}



// Can you see this?
//// another line
////11111
//2222
