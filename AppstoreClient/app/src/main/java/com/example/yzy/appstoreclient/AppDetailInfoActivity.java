package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by yzy on 15-2-17.
 */
public class AppDetailInfoActivity extends Activity{
    private static final String TAG = "AppDetailInfoActivity";
    TextView tvAppName = null;
    ImageView iconImage = null;
    ImageView photoImage = null;
    AppInfo mAppInfo;
    ProgressDialog dialog = null;
    Button btnInstall = null;
    protected static final int DOWNSUCCESS = 0;// "downlaod_and_install_done";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        mAppInfo =(AppInfo)intent.getSerializableExtra("appinfo");
        setContentView(R.layout.app_detail);
        initView();

    }

    private void initView() {
        tvAppName = (TextView) findViewById(R.id.name);
        tvAppName.setText(mAppInfo.getName());



        btnInstall = (Button) findViewById(R.id.btnInstall);
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new DownAndInstallThread()).start();
                dialog = ProgressDialog.show(AppDetailInfoActivity.this, "",
                        "Loading. Please wait...", true);
            }
        });


        iconImage = (ImageView) findViewById(R.id.appicon);
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL uri = new URL(mAppInfo.getIconUrl());

                    Log.d("yzy","mAppInfo.getIconUrl()="+mAppInfo.getIconUrl());
                    final Bitmap bitmap = BitmapFactory.decodeStream(uri.openStream());
                    iconImage.post(new Runnable() {
                        @Override
                        public void run() {
                            iconImage.setImageBitmap(bitmap);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
        photoImage = (ImageView) findViewById(R.id.appphoto);
        Thread t2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL uri = new URL(mAppInfo.getPhotoUrl());
                    final Bitmap bitmap = BitmapFactory.decodeStream(uri.openStream());
                    photoImage.post(new Runnable() {
                        @Override
                        public void run() {
                            photoImage.setImageBitmap(bitmap);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        t2.start();
    }

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNSUCCESS:
                    dialog.dismiss();
                    Toast.makeText(AppDetailInfoActivity.this, "安装成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
    // 主线程Handler负责更新UI，Handler与 Thread通过Message通信
    class DownAndInstallThread implements Runnable{

        @Override
        public void run() {

            Log.d("yzy", "download start..."+mAppInfo.getApkUrl());
            //.http://bcs.duapp.com/yzy20120930/luck.apk 解析出luck.apk
            File apkFile = downLoadFile(mAppInfo.getApkUrl());
            Log.d("yzy", "download done...");

            openFile(apkFile);
            Message msg = new Message();
            msg.what = DOWNSUCCESS;
            AppDetailInfoActivity.this.myHandler.sendMessage(msg);

        }

    }

    private void openFile(File file) {
        // TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
    }


    public static File downLoadFile(String httpUrl) {
        File tmpFile = new File("//sdcard");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        String fileName = httpUrl.split("/")[httpUrl.split("/").length-1];

        final File file = new File("//sdcard//" + fileName);
        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                double count = 0;
                if (conn.getResponseCode() >= 400) {
                    // Log.i("time","time exceed");
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }
                        } else {
                            break;
                        }
                    }
                }
                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return file;
    }

}
