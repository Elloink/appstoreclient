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
import android.view.MenuItem;
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
public class AppDetailInfoActivity extends BaseActivity{
    private static final String TAG = "AppDetailInfoActivity";
    TextView tvAppName = null;
    TextView tvAppSummary = null;
    TextView tvAppCategory = null;
    ImageView iconImage = null;
    ImageView photoImage = null;
    ImageView photoImage2 = null;
    ImageView photoImage3 = null;
    AppInfo mAppInfo;
    ProgressDialog dialog = null;
    Button btnInstall = null;
    protected static final int DOWNSUCCESS = 0;// "downlaod_and_install_done";
    protected static final int DOWNLOADPROGRESS = 1;// "downlaod_and_install_done";
    protected static final int DOWNLOADCANCEL = 2;
    protected static final int DOWNLOADDONE = 3;
    DownAndInstallThread mDownAndInstallThread = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        mAppInfo =(AppInfo)intent.getSerializableExtra("appinfo");
        setContentView(R.layout.app_detail);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mDownAndInstallThread = null;
    }

    private void initView() {

        getActionBar().setTitle(mAppInfo.getName());


        tvAppName = (TextView) findViewById(R.id.name);
        tvAppName.setText(mAppInfo.getName());

    //    tvAppCategory = (TextView) findViewById(R.id.category);
    //    tvAppCategory.setText(mAppInfo.getCategoryName());



        btnInstall = (Button) findViewById(R.id.btnInstall);
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (mDownAndInstallThread == null) { //第一次下载，或者暂停之后下载
                        mDownAndInstallThread = new DownAndInstallThread(mAppInfo.getApkUrl());
                        mDownAndInstallThread.startDownload();
                        mDownAndInstallThread.start();
                        //btnInstall.setText("暂停");

                    } else {//不是空，说明是想暂停
                        mDownAndInstallThread.interrupt();
                        mDownAndInstallThread.stopDownload();
                        Log.d("yzy", "mDownAndInstallThread  interrupt..");
                        mDownAndInstallThread = null;

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
                                    if (mDownAndInstallThread == null) { //第一次下载，或者暂停之后下载
                                        mDownAndInstallThread = new DownAndInstallThread(mAppInfo.getApkUrl());
                                        mDownAndInstallThread.startDownload();
                                        mDownAndInstallThread.start();
                                        //btnInstall.setText("暂停");

                                    } else {//不是空，说明是想暂停
                                        mDownAndInstallThread.interrupt();
                                        mDownAndInstallThread.stopDownload();
                                        Log.d("yzy", "mDownAndInstallThread  interrupt..");
                                        mDownAndInstallThread = null;

                                    }

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
        photoImage2 = (ImageView) findViewById(R.id.appphoto2);
        photoImage3 = (ImageView) findViewById(R.id.appphoto3);
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
                            photoImage2.setImageBitmap(bitmap);
                            photoImage3.setImageBitmap(bitmap);


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
                    btnInstall.setText("暂停"+ " 已下载"+msg.arg1+"%");
                    break;
                case DOWNLOADCANCEL:
                    btnInstall.setText("继续");
                    break;
                case DOWNLOADDONE:
                    btnInstall.setText("安装");
                    break;
            }
        }
    };
    // 主线程Handler负责更新UI，Handler与 Thread通过Message通信
    class DownAndInstallThread extends Thread{


        String apkUrl;
        SuspendableDownloader suspendableDownloader = new SuspendableDownloader();

        public DownAndInstallThread(String apkUrl) {
              this.apkUrl = apkUrl;
        }
        public void stopDownload(){
            suspendableDownloader.stopDownload();
        }

        public void startDownload(){
            suspendableDownloader.startDownload();
        }
        @Override
        public void run() {

            Log.d("yzy", "download start..."+apkUrl);
            //.http://bcs.duapp.com/yzy20120930/luck.apk 解析出luck.apk
            File apkFile = null;
            try {


                suspendableDownloader.setCallBack(new SuspendableDownloader.CallBack() {
                    @Override
                    public boolean notfiyProgress(int percent) {
                        //子线程

                        Message message = myHandler.obtainMessage();
                        message.what = DOWNLOADPROGRESS;
                        message.arg1 = percent;
                        myHandler.sendMessage(message);
                        return false;
                    }

                    @Override
                    public void onDownLoadCancel() {
                        Message message = myHandler.obtainMessage();
                        message.what = DOWNLOADCANCEL;
                        myHandler.sendMessage(message);

                    }

                    @Override
                    public void onDownLoadDone() {
                        Message message = myHandler.obtainMessage();
                        message.what = DOWNLOADDONE;
                        myHandler.sendMessage(message);
                    }
                });
                String filePath = suspendableDownloader.downLoadFile(apkUrl);
                apkFile = new File(filePath);
                Log.d("yzy","apkFile = "+apkFile);
                if (!suspendableDownloader.isStopDownload) {//说明没有下载完成，用户点击了暂停
                    openFile(apkFile);
                    Message msg = new Message();
                    msg.what = DOWNSUCCESS;
                    myHandler.sendMessage(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("yzy", "download done...");




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
