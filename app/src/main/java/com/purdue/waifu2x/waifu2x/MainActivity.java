package com.purdue.waifu2x.waifu2x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;




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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imageView1;
    Uri selectedImage;
    private final  String TAG="MainActivity";
    private Button buttonUpLoad = null;
    private Button buttonDownLoad = null;
    private SFTPUtils sftp;
    TextView txt;
    TextView txt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        txt=(TextView)findViewById(R.id.textView4);
        txt2=(TextView)findViewById(R.id.textView6);
        // Request permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            }
        }
    }
    public void init(){
        //获取控件对象
        buttonUpLoad = (Button) findViewById(R.id.button2);
        //buttonDownLoad = (Button) findViewById(R.id.button_download);
        //设置控件对应相应函数
        buttonUpLoad.setOnClickListener(this);
//        sftp = new SFTPUtils("SFTP服务器IP", "用户名","密码");
        sftp = new SFTPUtils("35.237.124.24", "ftpuser","uiuc626");
//        sftp = new SFTPUtils("35.229.127.84", "ftpuser","uiuc626");
    }
    public void choose_image(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        imageView1=(ImageView) findViewById(R.id.imageView);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    imageView1.setImageURI(selectedImage);
                    String s = getRealPathFromURI(selectedImage);
                    String s2 = getRealPathFromURI2(selectedImage);
                    txt2.setText(s2);
                    txt.setText(s);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    imageView1.setImageURI(selectedImage);
                    String s = getRealPathFromURI(selectedImage);
                    txt.setText(s);
                    String s2 = getRealPathFromURI2(selectedImage);
                    txt2.setText(s2);
                }
                break;
        }

    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DISPLAY_NAME};
        //image name
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    public String getRealPathFromURI2(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA};
        //image path
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }
    public class SFTPUtils {

        private String TAG = "SFTPUtils";
        private String host;
        private String username;
        private String password;
        private int port = 22;
        private ChannelSftp sftp = null;
        private Session sshSession = null;

        public SFTPUtils(String host, String username, String password) {
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
                    Log.d(TAG, "sftp is closed already");
                }
            }
            if (this.sshSession != null) {
                if (this.sshSession.isConnected()) {
                    this.sshSession.disconnect();
                    Log.d(TAG, "sshSession is closed already");
                }
            }
        }

        /**
         * 单个文件下载
         *
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
                File file = new File(localPath + localFileName);
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
         *
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
                Log.d(TAG, "手机地址" + localPath);
                Log.d(TAG, "远程地址" + remotePath + remoteFileName);
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


        /**
         * 批量上传
         * @param remotePath
         * @param localPath
         * @param del
         * @return
         */
        public boolean bacthUploadFile(String remotePath, String localPath,
                                       boolean del) {
            try {
                File file = new File(localPath);
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()
                            && files[i].getName().indexOf("bak") == -1) {
                        synchronized(remotePath){
                            if (this.uploadFile(remotePath, files[i].getName(),
                                    localPath, files[i].getName())
                                    && del) {
                                deleteFile(localPath + files[i].getName());
                            }
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.disconnect();
            }
            return false;

        }

        /**
         * 批量下载文件
         *
         * @param remotPath
         *            远程下载目录(以路径符号结束)
         * @param localPath
         *            本地保存目录(以路径符号结束)
         * @param fileFormat
         *            下载文件格式(以特定字符开头,为空不做检验)
         * @param del
         *            下载后是否删除sftp文件
         * @return
         */
        @SuppressWarnings("rawtypes")
        public boolean batchDownLoadFile(String remotPath, String localPath,
                                         String fileFormat, boolean del) {
            try {
                connect();
                Vector v = listFiles(remotPath);
                if (v.size() > 0) {

                    Iterator it = v.iterator();
                    while (it.hasNext()) {
                        ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
                        String filename = entry.getFilename();
                        SftpATTRS attrs = entry.getAttrs();
                        if (!attrs.isDir()) {
                            if (fileFormat != null && !"".equals(fileFormat.trim())) {
                                if (filename.startsWith(fileFormat)) {
                                    if (this.downloadFile(remotPath, filename,
                                            localPath, filename)
                                            && del) {
                                        deleteSFTP(remotPath, filename);
                                    }
                                }
                            } else {
                                if (this.downloadFile(remotPath, filename,
                                        localPath, filename)
                                        && del) {
                                    deleteSFTP(remotPath, filename);
                                }
                            }
                        }
                    }
                }
            } catch (SftpException e) {
                e.printStackTrace();
            } finally {
                this.disconnect();
            }
            return false;
        }


        /**
         * 删除文件
         * @param filePath
         * @return
         */
        public boolean deleteFile(String filePath) {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            if (!file.isFile()) {
                return false;
            }
            return file.delete();
        }

        public boolean createDir(String createpath) {
            try {
                if (isDirExist(createpath)) {
                    this.sftp.cd(createpath);
                    Log.d(TAG,createpath);
                    return true;
                }
                String pathArry[] = createpath.split("/");
                StringBuffer filePath = new StringBuffer("/");
                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }
                    filePath.append(path + "/");
                    if (isDirExist(createpath)) {
                        sftp.cd(createpath);
                    } else {
                        sftp.mkdir(createpath);
                        sftp.cd(createpath);
                    }
                }
                this.sftp.cd(createpath);
                return true;
            } catch (SftpException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * 判断目录是否存在
         * @param directory
         * @return
         */
        @SuppressLint("DefaultLocale")
        public boolean isDirExist(String directory) {
            boolean isDirExistFlag = false;
            try {
                SftpATTRS sftpATTRS = sftp.lstat(directory);
                isDirExistFlag = true;
                return sftpATTRS.isDir();
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().equals("no such file")) {
                    isDirExistFlag = false;
                }
            }
            return isDirExistFlag;
        }

        public void deleteSFTP(String directory, String deleteFile) {
            try {
                sftp.cd(directory);
                sftp.rm(deleteFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 创建目录
         * @param path
         */
        public void mkdirs(String path) {
            File f = new File(path);
            String fs = f.getParent();
            f = new File(fs);
            if (!f.exists()) {
                f.mkdirs();
            }
        }

        /**
         * 列出目录文件
         * @param directory
         * @return
         * @throws SftpException
         */

        @SuppressWarnings("rawtypes")
        public Vector listFiles(String directory) throws SftpException {
            return sftp.ls(directory);
        }


    }

    public void convert(View view) {

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
        // TODO Auto-generated method stub
        new Thread() {
            @Override
            public void run() {

                //这里写入子线程需要做的工作

                switch (v.getId()) {
                    case R.id.button2: {
                        String txt1 = txt.getText().toString();
                        String txt = txt2.getText().toString();

                        //上传文件
                        Log.d(TAG,"上传文件");
//                        String localPath = "sdcard/xml/";
                        String localPath = "sdcard/Pictures/";
//                        String remotePath = "test";
                        String remotePath = "/ftpuser/";
                        sftp.connect();
                        Log.d(TAG,"连接成功");
//                        sftp.uploadFile(remotePath,"APPInfo.xml", localPath, "APPInfo.xml");
                        sftp.uploadFile(remotePath,txt1, txt, txt1);
                        Log.d(TAG,"上传成功");
                        sftp.disconnect();
                        Log.d(TAG,"断开连接");

                    }
                    break;


                }
            }
        }.start();
        Intent mIntent=new Intent(this, Main2Activity.class);
        mIntent.putExtra("Image", selectedImage.toString());
        mIntent.putExtra("Image_name", txt.getText().toString());
        startActivity(mIntent);
    };

    }

