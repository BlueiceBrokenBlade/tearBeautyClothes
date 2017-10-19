package com.imooc.tearbeautifulclothes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by xhx12366 on 2017-05-28.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private Integer[] images;


    public ImageAdapter(Context context,Integer[] images){
        this.context=context;
        this.images=images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if(convertView==null){
            image=new ImageView(context);
            image.setLayoutParams(new GridView.LayoutParams(350,350));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);//(放大居中)设置图片在View上显示的样式
            image.setPadding(8,8,8,8);
            convertView=image;
        }else{
            image=(ImageView)convertView;
        }

        image.setImageResource(images[position]);
        return image;
    }
}
