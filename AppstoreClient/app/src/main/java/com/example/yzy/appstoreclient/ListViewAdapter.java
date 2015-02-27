package com.example.yzy.appstoreclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by yangzhongyu on 15-2-27.
 */
class ListViewAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<AppInfo> appInfos;
    Context context;

    public ListViewAdapter(Context context, List<AppInfo> appInfos) {
        inflater = LayoutInflater.from(context);
        this.appInfos = appInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return appInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return appInfos.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final  int position, View convertView, ViewGroup parent) {
        //to do:
        // 1.convertView
        // 2.holder避免多次findViewById
        //3.内存缓存bitmap
        final AppInfo info = appInfos.get(position);
        View view = inflater.inflate(R.layout.gv_item, null);
        final TextView tv = (TextView) view.findViewById(R.id.gv_item_appname);
        final ImageView iv = (ImageView) view.findViewById(R.id.gv_item_icon);
        tv.setText(info.getName());
        if (info.getAppIcon() != null ) {
            //小白：不要把这个非网络请求的操作也用view.post(Runnable)去做，直接主线程完成
            //因为post的Runnable并不是立即执行的，是异步的
            iv.setImageBitmap(BitmapFactory.decodeByteArray(info.getAppIcon(), 0, info.getAppIcon().length));
        } else {
            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();

                    try {
                        if (info.getAppIcon() == null && !info.getIconUrl().isEmpty()) {
                            URL uri = new URL(info.getIconUrl());
                            //把Bitmap转换成byte[ ]
                            final Bitmap bitmap = BitmapFactory.decodeStream(uri.openStream());
                            //实例化字节数组输出流
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
                            info.setAppIcon(baos.toByteArray());
                            Log.d("yzy", "decode " + info.getName());
                            //   info.setAppIcon(bitmap);
                        }

                        if (!info.getIconUrl().isEmpty()) {
                            iv.post(new Runnable() {
                                @Override
                                public void run() {
                                    iv.setImageBitmap(BitmapFactory.decodeByteArray(info.getAppIcon(), 0,info.getAppIcon().length));

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


        Button btnDetail = (Button) view.findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppInfo app = appInfos.get(position);
                Intent intent = new Intent(context,AppDetailInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appinfo", app);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return view;
    }

}