package com.example.yzy.appstoreclient;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by yangzhongyu on 15-3-4.
 */
public class SuspendableDownloader {
    /* 已下载文件长度 */
   // private static long downloadSize = 0;

    /* 原始文件长度 */
  //  private static int fileSize = 0;


  //  private  static HashMap<String,Long>  mApkLoadedSizeMap = new HashMap<String, Long>();


    public interface CallBack{
        public boolean notfiyProgress(int percent);
    }
    private   CallBack mCallBack = null;
    public  void setCallBack(CallBack callback){
        this.mCallBack = callback;
    }
    public  String downLoadFile(String httpUrl) throws IOException {
        File tmpFile = new File("//sdcard");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        String fileName = httpUrl.split("/")[httpUrl.split("/").length-1];

       // File myTempFile = new File(filePath + "/" + filename);
        final RandomAccessFile file = new RandomAccessFile("//sdcard//" + fileName,"rwd");

        if (file.length() == 0) {
            //第一次下载
            Log.d("yzy","first time=");
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int  length = conn.getContentLength();
           // file.setLength(length);

           // Log.d("yzy", "文件总长度=" + fileSize);
            InputStream is = conn.getInputStream();
            //FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[256];
            conn.connect();
            double count = 0;
            if (conn.getResponseCode() >= 400) {
                // Log.i("time","time exceed");
            } else {
                int downloadsize = 0;
                while (count <= 100) {
                    if (is != null) {
                        int numRead = is.read(buf);
                        downloadsize += numRead;
                        mCallBack.notfiyProgress(downloadsize*100/length);
                        if (numRead <= 0) {
                            break;
                        } else {
                            file.write(buf, 0, numRead);
                        }
                    } else {
                        break;
                    }
                }
            }
            conn.disconnect();
            file.close();
            is.close();
        }else {


            Log.d("yzy","continue file.length() ="+file.length()  );
            file.seek(file.length());

            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int length = conn.getContentLength();


            HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();

            conn2.setRequestProperty("Range", "bytes="+file.length()+"-"+length); //如果文件已经下载完成，即从 1234-1234 会报告FileNotFound Exception

            //java.lang.IllegalStateException: Cannot set request property after connection is made

          //  int length = conn.getContentLength();http://www.eoeandroid.com/thread-154241-1-1.html



            if (file.length() == length) {
                 return "//sdcard//" + fileName;
            }

            // file.setLength(length);

            InputStream is = conn.getInputStream();
            //FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[256];
            conn.connect();
            double count = 0;

            Log.d("yzy","conn.getResponseCode()="+conn.getResponseCode());
            if (conn.getResponseCode() >= 400) {
                // Log.i("time","time exceed");
            } else {
                while (count <= 100) {
                    if (is != null) {
                        int numRead = is.read(buf);
                        if (numRead <= 0) {
                            break;
                        } else {
                            file.write(buf, 0, numRead);
                        }
                    } else {
                        break;
                    }
                }
            }
            conn.disconnect();
            file.close();
            is.close();


        }

        return "//sdcard//" + fileName;
    }

}
