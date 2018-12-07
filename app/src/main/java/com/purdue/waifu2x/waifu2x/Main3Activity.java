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

        final ImageView image1 = findViewById(R.id.imageViewA);
        final ImageView image2 = findViewById(R.id.imageViewB);
        final ImageView image3 = findViewById(R.id.imageViewC);
        final ImageView image4 = findViewById(R.id.imageViewD);
        final ImageView image5 = findViewById(R.id.imageViewE);
        final ImageView image6 = findViewById(R.id.imageViewF);
        final ImageView image7 = findViewById(R.id.imageViewG);
        final ImageView image8 = findViewById(R.id.imageViewH);
        final ImageView image9 = findViewById(R.id.imageViewI);
        final ImageView image10 = findViewById(R.id.imageViewJ);
        final ImageView image11 = findViewById(R.id.imageViewK);
        final ImageView image12 = findViewById(R.id.imageViewL);
        final ImageView image13 = findViewById(R.id.imageViewM);
        final ImageView image14 = findViewById(R.id.imageViewN);
        final ImageView image15 = findViewById(R.id.imageViewO);
        final ImageView image16 = findViewById(R.id.imageViewP);

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
                    dbHandler.deleteImage(i);
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

    public void toDisplay (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        waifuImage wi;
        String imagePath = null;

        Intent mIntent = new Intent(this, DisplayActivity.class);
        switch (view.getId()) {
            case R.id.imageViewA:
                wi = dbHandler.findImage(1);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewB:
                wi = dbHandler.findImage(2);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewC:
                wi = dbHandler.findImage(3);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewD:
                wi = dbHandler.findImage(4);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewE:
                wi = dbHandler.findImage(5);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewF:
                wi = dbHandler.findImage(6);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewG:
                wi = dbHandler.findImage(7);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewH:
                wi = dbHandler.findImage(8);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewI:
                wi = dbHandler.findImage(9);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewJ:
                wi = dbHandler.findImage(10);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewK:
                wi = dbHandler.findImage(11);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewL:
                wi = dbHandler.findImage(12);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewM:
                wi = dbHandler.findImage(13);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewN:
                wi = dbHandler.findImage(14);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewO:
                wi = dbHandler.findImage(15);
                imagePath = wi.get_imagePath();
                break;
            case R.id.imageViewP:
                wi = dbHandler.findImage(16);
                imagePath = wi.get_imagePath();
                break;
        }//end switch

        if (imagePath == null) {
            Toast.makeText(this, "Not a valid image", Toast.LENGTH_SHORT).show();
        } else {
            mIntent.putExtra("Path", imagePath);
            startActivity(mIntent);
        }
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
