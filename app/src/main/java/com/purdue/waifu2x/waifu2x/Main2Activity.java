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
import android.widget.TextView;
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
import java.util.Arrays;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    String filePath;
//    String cachePath;
//    String fileName;
    boolean flip = true;
    ImageView image_new;
    ImageView image_old;
    private final  String TAG="MainActivity";
    private Button buttonUpLoad = null;
    private Button buttonDownLoad = null;
    private SFTPUtils sftp;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();

        image_new = findViewById(R.id.imageView);
        image_old = findViewById(R.id.imageView_old);
        ImageView dl = findViewById(R.id.imageView3);
        dl.setClickable(true);

        Intent mIntent = getIntent();
        String uriString = mIntent.getStringExtra("Image");
        Uri getImage = Uri.parse(uriString);
        String imagename=mIntent.getStringExtra("Image_name");
        txt=(TextView)findViewById(R.id.textView7);
        txt.setText(imagename);
        try {
            InputStream is = getContentResolver().openInputStream(getImage);
            image_old.setImageDrawable(Drawable.createFromStream(is, getImage.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    public void init(){
        //获取控件对象
//        buttonDownLoad = (Button) findViewById(R.id.button9);
//        //设置控件对应相应函数
//        buttonDownLoad.setOnClickListener(this);
//        sftp = new SFTPUtils("SFTP服务器IP", "用户名","密码");
        sftp = new SFTPUtils("35.237.124.24", "ftpuser","uiuc626");
//        sftp = new SFTPUtils("35.229.127.84", "ftpuser","uiuc626");
    }

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
        File file = new File(filePath);
        Uri imageUri = FileProvider.getUriForFile(this, "com.purdue.waifu2x.waifu2x.fileprovider", file);
        shareImage.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareImage.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareImage, "Share via"));
    }

/*    public void download(View view) {

        //Creating a folder to save images in
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + "/Waifu2x_Images/");

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

    }*/

/*    public void downloadToFile (String source, String destination) throws IOException {

        File sourceFile = new File(source);
        File destFile = new File(destination);
        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];
        try {
            InputStream in = getContentResolver().openInputStream(Uri.fromFile(sourceFile));
            OutputStream out = new FileOutputStream(destFile);
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.getStackTrace(); }

    }*/

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

    @Override
    public void onClick(final View v) {
        final String txt1 = txt.getText().toString();
        final String localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + "/Waifu2x_Images/";

        File dir = new File(localPath);
        dir.mkdirs();
        new Thread() {
            @Override
            public void run() {

                //这里写入子线程需要做的工作

                switch (v.getId()) {

                    case R.id.imageView3: {
                        //下载文件
                        Log.d(TAG,"下载文件");
//                        String remotePath = "test";
                        String remotePath = "/photo/";
                        sftp.connect();
                        Log.d(TAG,"连接成功");
                        sftp.downloadFile(remotePath, txt1, localPath, txt1);
                        Log.d(TAG,"下载成功");
                        sftp.disconnect();
                        Log.d(TAG,"断开连接");

                    }
                    break;
                    default:
                        break;
                }
            }
        }.start();
        File file =  new File(localPath + txt1);
        filePath = file.getAbsolutePath();
        if (file.exists()) {
            image_new.setImageDrawable(Drawable.createFromPath(filePath));

            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
            waifuImage wi = new waifuImage(1, filePath);
            dbHandler.addImage(wi);

            //Adding image to media library
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            Main2Activity.this.sendBroadcast(mediaScanIntent);
        } else {
            Toast.makeText(this, "Still converting, please wait", Toast.LENGTH_SHORT).show();
        }
    };

    public class SFTPUtils {

        private String TAG="SFTPUtils";
        private String host;
        private String username;
        private String password;
        private int port = 22;
        private ChannelSftp sftp = null;
        private Session sshSession = null;

        public SFTPUtils (String host, String username, String password) {
            this.host = host;
            this.username = username;
            this.password = password;
        }

        /**
         * connect server via sftp
         */
        public ChannelSftp connect() {
            JSch jsch = new JSch();
            try {
                sshSession = jsch.getSession(username, host, port);
                sshSession.setPassword(password);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                Channel channel = sshSession.openChannel("sftp");
                if (channel != null) {
                    channel.connect();
                } else {
//                    Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "channel connecting failed.");
                }
                sftp = (ChannelSftp) channel;
            } catch (JSchException e) {
                e.printStackTrace();
            }
            return sftp;
        }


        /**
         * 断开服务器
         */
        public void disconnect() {
            if (this.sftp != null) {
                if (this.sftp.isConnected()) {
                    this.sftp.disconnect();
                    Log.d(TAG,"sftp is closed already");
                }
            }
            if (this.sshSession != null) {
                if (this.sshSession.isConnected()) {
                    this.sshSession.disconnect();
                    Log.d(TAG,"sshSession is closed already");
                }
            }
        }

        /**
         * 单个文件下载
         * @param remotePath
         * @param remoteFileName
         * @param localPath
         * @param localFileName
         * @return
         */
        public boolean downloadFile(String remotePath, String remoteFileName,
                                    String localPath, String localFileName) {
            try {
                sftp.cd(remotePath);
                File file = new File(localPath+localFileName);
//                mkdirs(localPath + localFileName);
                sftp.get(remoteFileName, new FileOutputStream(file));
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            }

            return false;
        }

        /**
         * 单个文件上传
         * @param remotePath
         * @param remoteFileName
         * @param localPath
         * @param localFileName
         * @return
         */
        public boolean uploadFile(String remotePath, String remoteFileName,
                                  String localPath, String localFileName) {
            FileInputStream in = null;
            try {
//                createDir(remotePath);
                System.out.println(remotePath);
                File file = new File(localPath);
                in = new FileInputStream(file);
                System.out.println(in);
                // 自己加的 debug
                Log.d(TAG,"手机地址" + localPath);
                Log.d(TAG,"远程地址" + remotePath + remoteFileName);
                // sftp_put(self, localfile, remotefile):
                sftp.put(in, remotePath + remoteFileName);
                System.out.println(sftp);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    }
}




