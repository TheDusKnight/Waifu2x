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
    Uri selectedImage;
    private String creatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gainCurrenTime();
        imageView1 = findViewById(R.id.imageView);
    }
/*
    * This is for downloading the uploaded image, which we do not need
    private void gainCurrenTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        creatTime = formatter.format(curDate);
    }
*/

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
        // 使用意图直接调用手机相册
        Intent intentPhoto = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 打开手机相册,设置请求码
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

/*        FileOutputStream b = null;
        String name = creatTime + ".jpg";
        File file = new File(getAlbumStorageDir("upload"), name);
        try {
            b = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (b != null) {
                b.flush();
                b.close();
            }
        }*/
        imageView1.setImageBitmap(bitmap);
/*       Not needed because we don't need to add the uploaded image to Photos/Images Library on phone
 *       Edit: We do need to save the image to a file for uploading to server, so we're uploading the image into the cache
 *       Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
 *       Uri contentUri = Uri.fromFile(file);
 *       mediaScanIntent.setData(contentUri);
 *       MainActivity.this.sendBroadcast(mediaScanIntent);*/
    }

    public void convert(View view) {
        Intent mIntent=new Intent(this, Main2Activity.class);
        mIntent.putExtra("Image", selectedImage.toString());

        //Uploading image into the cache to later upload to server
        //Getting file extension first
        String ext;
        //Check uri format to avoid null
        if (selectedImage.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            ext = mime.getExtensionFromMimeType(this.getContentResolver().getType(selectedImage));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            ext = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(selectedImage.getPath())).toString());
        }
        File file = new File(getCacheDir(), "old_" + System.currentTimeMillis() + "." + ext);

        //Writing image to new file
        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];
        try {
            InputStream in = getContentResolver().openInputStream(selectedImage);
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
//        } finally {
//            in.close();
//            out.close();
//        }

        //Checking for network permissions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 4);
            } else {
                uploadToServer(file);
            }
        }
        startActivity(mIntent);
    }

    public void uploadToServer (final File file) {
        if (NetWorkUtil.isNetworkAvailable(MainActivity.this)) {
            new Thread() {
                @Override
                public void run() {
                    aboutTakePhotoUp(file);
                }
            }.start();
        } else {
            Toast.makeText(MainActivity.this, "对不起，没有网络！", Toast.LENGTH_SHORT).show();
        }
    }
    private void aboutTakePhotoUp(File photoFile) {
        EditText UserText = findViewById(R.id.editText);
        EditText PassText = findViewById(R.id.editText2);
        try {
            FileInputStream in = new FileInputStream(photoFile);
            //将下面的信息换成自己需要的即可
            boolean flag = MainActivity.FileTool.uploadFile("35.196.122.161", 22,
                    UserText.getText().toString(), PassText.getText().toString(), "/photo/", photoFile.getName(), in);
            System.out.println(photoFile.getName());
//            boolean flag = FileTool.uploadFile("149.28.115.237", 21,"ftpuser", "uiuc626", "/photo/", "Hello.jpg", in);

            //Deleting file from cache after upload is complete
            boolean deleted = photoFile.delete();
            if (flag == true) {
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(MainActivity.this, "Success！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "Fail！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public static class FileTool {
        /**
         * Description: 向FTP服务器上传文件
         *
         * @param url
         *            FTP服务器hostname
         * @param port
         *            FTP服务器端口
         * @param username
         *            FTP登录账号
         * @param password
         *            FTP登录密码
         * @param path
         *            FTP服务器保存目录，是linux下的目录形式,如/photo/
         * @param filename
         *            上传到FTP服务器上的文件名,是自己定义的名字，
         * @param input
         *            输入流
         * @return 成功返回true，否则返回false
         */
        public static boolean uploadFile(String url, int port, String username, String password,
                                         String path, String filename, InputStream input) {
            boolean success = false;
            FTPClient ftp = new FTPClient();
            // LogUitl.Infor(url+port+username+password+path);

            try {
                int reply;
                ftp.connect(url, port);// 连接FTP服务器
                // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
                ftp.login(username, password);//登录
                reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    return success;
                }
                ftp.setFileType(FTP.BINARY_FILE_TYPE);//上传上去的图片数据格式（）一定要写这玩意，不然在服务器就打不开了
                if (!ftp.changeWorkingDirectory(path)) {
                    if (ftp.makeDirectory(path)) {
                        ftp.changeWorkingDirectory(path);
                    }
                }
                //  ftp.changeWorkingDirectory(path);
                //设置成其他端口的时候要添加这句话
                ftp.enterLocalPassiveMode(); // 一定要加这个
                ftp.storeFile(filename, input);
                input.close();
                ftp.logout();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch (IOException ioe) {
                    }
                }
            }
            return success;
        }


        public static Boolean downloadAndSaveFile(String server, int portNumber,
                                                  String user, String password, String filename, File localFile)
                throws IOException {
            FTPClient ftp = null;

            try {
                ftp = new FTPClient();
                ftp.connect(server, portNumber);
//                Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

                ftp.login(user, password);
//                Log.d(LOG_TAG, "Logged in");
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
//                Log.d(LOG_TAG, "Downloading");
                ftp.enterLocalPassiveMode();

                OutputStream outputStream = null;
                boolean success = false;
                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(
                            localFile));
                    success = ftp.retrieveFile(filename, outputStream);
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }

                return success;
            } finally {
                if (ftp != null) {
                    ftp.logout();
                    ftp.disconnect();
                }
            }
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
