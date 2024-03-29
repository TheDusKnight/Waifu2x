package com.purdue.waifu2x.waifu2x;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;

import static android.media.ThumbnailUtils.extractThumbnail;

public class Main3Activity extends AppCompatActivity {

    int width;
    int height;
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

        //Making table fixed size
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(outSize.x,outSize.y);
        TableLayout library = new TableLayout(this);
        library.setLayoutParams(params);

        //getting width of each cell
        width = outSize.x/4;

        //getting height of bottom buttons in pixels
        int dp = 40;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        int px = (int) Math.ceil(dp * logicalDensity);
        
        //getting cell height by subtracting buttons from display first
        height = (outSize.y - px) / 4;

        image1.setImageBitmap(getWaifuImage(1));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image2.setImageBitmap(getWaifuImage(2));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image3.setImageBitmap(getWaifuImage(3));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image4.setImageBitmap(getWaifuImage(4));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image5.setImageBitmap(getWaifuImage(5));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image6.setImageBitmap(getWaifuImage(6));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image7.setImageBitmap(getWaifuImage(7));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image8.setImageBitmap(getWaifuImage(8));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image9.setImageBitmap(getWaifuImage(9));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image10.setImageBitmap(getWaifuImage(10));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image11.setImageBitmap(getWaifuImage(11));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image12.setImageBitmap(getWaifuImage(12));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image13.setImageBitmap(getWaifuImage(13));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image14.setImageBitmap(getWaifuImage(14));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image15.setImageBitmap(getWaifuImage(15));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
        image16.setImageBitmap(getWaifuImage(16));
        image1.setLayoutParams(new TableRow.LayoutParams(width, height));
    }

    private Bitmap getWaifuImage(int i) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        waifuImage wi = dbHandler.findImage(i);
        String imagePath = wi.get_imagePath();
        Bitmap result = null;
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
                    if (imagePath != null) {
                        file = new File(imagePath);
                        if (file.exists()) {
                            flag = false;
                        } else {
                            result = BitmapFactory.decodeResource(this.getResources(), R.drawable.image, null);
                            return result;

                        }
                    }
                } //end while
            }
            //if path does exist
            Bitmap b = BitmapFactory.decodeFile(imagePath);
            result = extractThumbnail(b, width, height);
            //d = Drawable.createFromPath(imagePath);

        } else {
            result = BitmapFactory.decodeResource(this.getResources(), R.drawable.image, null);
//            d = ResourcesCompat.getDrawable(getResources(), R.drawable.image, null);
        }
        return result;
        //return d;
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
