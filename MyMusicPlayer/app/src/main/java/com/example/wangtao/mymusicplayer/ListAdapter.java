package com.example.wangtao.mymusicplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wangtao on 2015/12/9.
 */
public class ListAdapter extends BaseAdapter
{
    ArrayList gcContent;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 

    ListAdapter(Context context,ArrayList gcContent)
    {
        Log.e("ListAdapter  ","success!!" );
        this.mInflater = LayoutInflater.from(context);
        this.gcContent=gcContent;
    }
    @Override
    public int getCount() {
        return gcContent.size();
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lyricslayout,null);

            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.content.setText(gcContent.get(position).toString());

        //Log.e("content is ", gcContent.get(position).toString());
        return convertView;
    }

    public final class ViewHolder//存放控件
    {
        TextView content;
    }
}