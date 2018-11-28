package com.purdue.waifu2x.waifu2x;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final ImageView image1 = findViewById(R.id.imageView7);
        final ImageView image2 = findViewById(R.id.imageView8);
        final ImageView image3 = findViewById(R.id.imageView9);
        final ImageView image4 = findViewById(R.id.imageView10);
        final ImageView image5 = findViewById(R.id.imageView11);
        final ImageView image6 = findViewById(R.id.imageView12);
        final ImageView image7 = findViewById(R.id.imageView13);
        final ImageView image8 = findViewById(R.id.imageView14);
        final ImageView image9 = findViewById(R.id.imageView15);
        final ImageView image10 = findViewById(R.id.imageView16);
        final ImageView image11 = findViewById(R.id.imageView17);
        final ImageView image12 = findViewById(R.id.imageView18);
        final ImageView image13 = findViewById(R.id.imageView19);
        final ImageView image14 = findViewById(R.id.imageView20);
        final ImageView image15 = findViewById(R.id.imageView21);
        final ImageView image16 = findViewById(R.id.imageView22);

        image1.setImageDrawable(getWaifuImage(1));
        image2.setImageDrawable(getWaifuImage(2));
        image3.setImageDrawable(getWaifuImage(3));
        image4.setImageDrawable(getWaifuImage(4));
        image5.setImageDrawable(getWaifuImage(5));
        image6.setImageDrawable(getWaifuImage(6));
        image7.setImageDrawable(getWaifuImage(7));
        image8.setImageDrawable(getWaifuImage(8));
        image9.setImageDrawable(getWaifuImage(9));
        image10.setImageDrawable(getWaifuImage(10));
        image11.setImageDrawable(getWaifuImage(11));
        image12.setImageDrawable(getWaifuImage(12));
        image13.setImageDrawable(getWaifuImage(13));
        image14.setImageDrawable(getWaifuImage(14));
        image15.setImageDrawable(getWaifuImage(15));
        image16.setImageDrawable(getWaifuImage(16));


    }

    private Drawable getWaifuImage(int i) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        waifuImage wi = dbHandler.findImage(i);
        String imagePath = wi.get_imagePath();
        Drawable d;
        if (imagePath != null) {
            //Check if path exists
            File file = new File(imagePath);
            if (!file.exists()) {
                boolean flag = true;
                while (flag) {
                    Toast.makeText(this, "An image has been moved or deleted", Toast.LENGTH_LONG).show();
                    for (int j = i + 1; j < 16; j++) {
                        //Updating database to fill in the gaps and checking again
                            dbHandler.fillMissingImage(j);
                    }
                    wi = dbHandler.findImage(i);
                    imagePath = wi.get_imagePath();
                    file = new File(imagePath);
                    if (imagePath != null) {
                        if (file.exists()) {
                            flag = false;
                        } else {
                            d = ResourcesCompat.getDrawable(getResources(), R.drawable.image, null);
                            return d;
                        }
                    }
                } //end while
            }
            //if path does exist
            d = Drawable.createFromPath(imagePath);
        } else {
            d = ResourcesCompat.getDrawable(getResources(), R.drawable.image, null);
        }
        return d;
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
