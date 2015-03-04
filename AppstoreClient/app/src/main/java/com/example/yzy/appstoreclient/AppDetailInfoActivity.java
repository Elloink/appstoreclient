package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    TextView tvAppSummary = null;
    ImageView iconImage = null;
    ImageView photoImage = null;
    AppInfo mAppInfo;
    ProgressDialog dialog = null;
    Button btnInstall = null;
    protected static final int DOWNSUCCESS = 0;// "downlaod_and_install_done";
    protected static final int DOWNLOADPROGRESS = 1;// "downlaod_and_install_done";
    DownAndInstallThread mDownAndInstallThread = null;

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
                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (mDownAndInstallThread == null) { //第一次下载，或者暂停之后下载
                        mDownAndInstallThread = new DownAndInstallThread(mAppInfo.getApkUrl());
                        mDownAndInstallThread.start();
                        //btnInstall.setText("暂停");

                    } else {//不是空，说明是想暂停
                        mDownAndInstallThread.interrupt();
                        mDownAndInstallThread = null;
                        btnInstall.setText("继续");
                    }

                    //dialog = ProgressDialog.show(AppDetailInfoActivity.this, "",
                   //         "正在下载安装文件...", true);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AppDetailInfoActivity.this);
                    AlertDialog dialog = null;
                    //  builder.setCancelable(false);
                    builder.setTitle("");
                    builder.setMessage("非Wifi网络下载会消耗数据网络流量");

                    builder.setPositiveButton("下载",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new DownAndInstallThread(mAppInfo.getApkUrl()).start();
                                    dialog = ProgressDialog.show(AppDetailInfoActivity.this, "",
                                            "正在下载安装文件...", true);
                                }
                            });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                   dialog.dismiss();
                                }
                            });
                    dialog = builder.show();
                }

            }
        });

        tvAppSummary = (TextView) findViewById(R.id.summary);
        tvAppSummary.setText(mAppInfo.getSummary());

        iconImage = (ImageView) findViewById(R.id.appicon);



        if(mAppInfo.getAppIcon() != null){
            iconImage.setImageBitmap(BitmapFactory.decodeByteArray(mAppInfo.getAppIcon(), 0,mAppInfo.getAppIcon().length));
        } else {

            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        if (!mAppInfo.getIconUrl().isEmpty()) {
                            URL uri = new URL(mAppInfo.getIconUrl());

                            final Bitmap bitmap = BitmapFactory.decodeStream(uri.openStream());
                            iconImage.post(new Runnable() {
                                @Override
                                public void run() {
                                    iconImage.setImageBitmap(bitmap);

                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            t.start();

        }


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
                    if(dialog != null){

                        dialog.dismiss();
                    }
                    Toast.makeText(AppDetailInfoActivity.this, "安装成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case DOWNLOADPROGRESS:
                    btnInstall.setText(msg.arg1+"");
                    break;
            }
        }
    };
    // 主线程Handler负责更新UI，Handler与 Thread通过Message通信
    class DownAndInstallThread extends Thread{


        String apkUrl;
        public DownAndInstallThread(String apkUrl) {
              this.apkUrl = apkUrl;
        }

        @Override
        public void run() {

            Log.d("yzy", "download start..."+apkUrl);
            //.http://bcs.duapp.com/yzy20120930/luck.apk 解析出luck.apk
            File apkFile = null;
            try {
                SuspendableDownloader suspendableDownloader = new SuspendableDownloader();

                suspendableDownloader.setCallBack(new SuspendableDownloader.CallBack() {
                    @Override
                    public boolean notfiyProgress(int percent) {
                        //子线程

                        Message message = new Message();
                        message.what = DOWNLOADPROGRESS;
                        message.arg1 = percent;
                        myHandler.sendMessage(message);
                        return false;
                    }
                });
                String filePath = suspendableDownloader.downLoadFile(apkUrl);
                apkFile = new File(filePath);
                Log.d("yzy","apkFile = "+apkFile);
                openFile(apkFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("yzy", "download done...");


            Message msg = new Message();
            msg.what = DOWNSUCCESS;
            myHandler.sendMessage(msg);

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

}
