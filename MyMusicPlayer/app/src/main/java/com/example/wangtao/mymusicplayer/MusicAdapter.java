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

public class MusicAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 
    private ArrayList<HashMap<String, Object>> data;

    public MusicAdapter(Context context,ArrayList<HashMap<String, Object>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data=data;

    }
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout,null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tit);//得到各个控件的对象
            holder.artist = (TextView) convertView.findViewById(R.id.art);
            holder.sig = (ImageView) convertView.findViewById(R.id.sig);
            convertView.setTag(holder);//绑定ViewHolder对象
        }
        else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.title.setText(data.get(position).get("title").toString());
        holder.artist.setText(data.get(position).get("artist").toString());
        if(data.get(position).get("isplay").toString()=="1")
        {
            holder.sig.setVisibility(View.VISIBLE);
        }else
        {
            holder.sig.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public final class ViewHolder//存放控件
    {
        TextView title,artist;
        ImageView sig;
    }
}
