package com.purdue.waifu2x.waifu2x;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.purdue.waifu2x.waifu2x.util.NetWorkUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    ImageView imageView1;
    Uri selectedImage = null;
    private String creatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.imageView);
    }

    public void choose_image(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            } else {
                openAlbum();
            }
        }
    }
    private void openAlbum() {
        Intent intentPhoto = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentPhoto, 1);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 4:
                if(grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this, "请允许读取相册！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            showImage(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showImage(Intent data) throws IOException {
        selectedImage = data.getData();
        Bitmap bitmap = null;
        try {
            InputStream is = getContentResolver().openInputStream(selectedImage);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView1.setImageBitmap(bitmap);
    }

    public void convert(View view) {
        Intent mIntent=new Intent(this, Main2Activity.class);
        if (selectedImage != null) {
            mIntent.putExtra("Image", selectedImage.toString());
            startActivity(mIntent);
        } else {
            Toast.makeText(this, "Oops, you need to select an image", Toast.LENGTH_SHORT).show();
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
