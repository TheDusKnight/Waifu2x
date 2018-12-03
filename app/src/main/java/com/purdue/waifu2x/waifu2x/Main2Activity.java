package com.purdue.waifu2x.waifu2x;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Main2Activity extends AppCompatActivity {

    String filePath;
    String cachePath;
    String fileName;
    boolean flip = true;
    ImageView image_new;
    ImageView image_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        image_new = findViewById(R.id.imageView);
        image_old = findViewById(R.id.imageView_old);
        ImageView dl = findViewById(R.id.imageView3);
        dl.setClickable(true);

        Intent mIntent = getIntent();
        String uriString = mIntent.getStringExtra("Image");
        Uri getImage = Uri.parse(uriString);

        try {
            InputStream is = getContentResolver().openInputStream(getImage);
            image_old.setImageDrawable(Drawable.createFromStream(is, getImage.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //still need converted image for image_new

        //preparing to store new image into cache and SQLite database

        //My test image
        //String example = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/pusheen-1.jpg";
        File file;
        fileName = "Waifu2x_" + System.currentTimeMillis() + ".png";
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        try {
            file = new File(getCacheDir(), fileName);
            cachePath = file.getAbsolutePath();
            //will need to edit if converted image does not come from file
            downloadToFile("", cachePath);

            //My test image
            //downloadToFile(example, cachePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        waifuImage wi = new waifuImage(1, cachePath);
        dbHandler.addImage(wi, wi.get_id());

        image_new.setImageDrawable(Drawable.createFromPath(wi.get_imagePath()));


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
        File file = new File(cachePath);
        Uri imageUri = FileProvider.getUriForFile(this, "com.purdue.waifu2x.waifu2x.fileprovider", file);
        shareImage.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareImage, "Share via"));
    }

    public void download(View view) {

        //Creating a folder to save images in
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + "/Waifu2x Images/");
        dir.mkdirs();

        File file = new File(dir, fileName);
        filePath = file.getAbsolutePath();
        try {
            downloadToFile(cachePath, filePath);
            Toast.makeText(this,"Image downloaded", Toast.LENGTH_LONG).show();
            view.setClickable(false);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image could not be downloaded", Toast.LENGTH_LONG).show();
        }

        //Adding image to media library
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        Main2Activity.this.sendBroadcast(mediaScanIntent);
    }

    public void downloadToFile (String source, String destination) throws IOException {
        File file = new File(source);
        Uri fileUri = Uri.fromFile(file);
        URL url = new URL(fileUri.toString());
        URLConnection connection = url.openConnection();
        connection.connect();
        int lengthOfFile = connection.getContentLength();
        Log.d("ANDRO_ASYNC", "Length of file: " + lengthOfFile);
        InputStream input = new BufferedInputStream(url.openStream());
        OutputStream output = new FileOutputStream(destination);
        byte data[] = new byte[1024];
        long total = 0;
        int count;
        while ((count = input.read(data)) != -1) {
            total += count;
            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        input.close();

    }

    public void undo(View view) {
        finish();
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
