package com.example.wangtao.mymusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wangtao on 2015/12/3.
 */
public class GetList
{
    public ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    private Context context;

    private int rate;
    public GetList(Context context)
    {
        this.context = context;
    }
    public ArrayList<HashMap<String, Object>> getSys()//从系统数据库获取播放列表
    {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Log.e("GetList", "getSys");
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                //String albumid = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                //String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String songid = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Long siz = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                String type = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));//类型
                if (siz > 1024 * 800) {
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    map.put("title", title);
                    map.put("artist", artist);
                    map.put("url", url);
                    map.put("isplay", "0");
                    map.put("songid", songid);

                    //Log.e("getlist.songid", id);
                    if(duration!=0)
                    {
                        rate=(int)(siz/duration)*8;
                        map.put("rate",String.valueOf(rate)+"kbps");//码率
                    }else
                    {
                        map.put("rate","未知");
                    }
                    data.add(map);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return data;
    }

}
