package com.example.wangtao.mymusicplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private int songindex = -1, lastsongindex = -1, isRandom = 0;//0是列表循环，1是随机，2是单曲循环
    private boolean isChanging = false;//判断进度条是否在使用
    MediaPlayer mp = new MediaPlayer();
    Boolean isCompleted = false;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
    Random random = new Random();
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new LocalBinder();
    }

    public void onCreate()//播放准备
    {
        super.onCreate();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                isCompleted = true;//播放完成
                Log.e("Service", "isCompleted=ture!!!!!!!!!!!!!!!");
            }
        });
    }
    public void next()
    {
            int l=getSongIndex();
            setLastSongIndex(l);//记录上一首歌
            switch (getIsRandom() % 3) {
                case 0://列表循环
                    if (getSongIndex() == data.size() - 1) {
                        setSongIndex(0);
                    } else {
                        SongIndexPuls();
                    }
                    break;
                case 1://随机播放
                    int s = random.nextInt(data.size() - 1);
                    setSongIndex(s);
                    break;
                case 2://单曲循环
                    if (getSongIndex() == data.size() - 1) {
                        setSongIndex(0);
                    }else
                    {
                        SongIndexPuls();
                    }
                    break;
                default:
                    break;
            }
        play(data.get(getSongIndex()).get("url").toString(), data.get(getSongIndex()).get("title").toString(), data.get(getSongIndex()).get("artist").toString());
    }

    @Override
    public void onDestroy() {
        mp.release();

        super.onDestroy();
    }

    public final class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public boolean isPlaying()//是否正在播放
    {
        return mp.isPlaying();
    }

    public int getDuration()//返回歌曲时间长度
    {
        return mp.getDuration();
    }

    public int getCurrentPosition()//返回当前播放时间
    {
        return mp.getCurrentPosition();
    }

    public void setLooping(boolean isLooping)//设置是否单曲循环
    {
        mp.setLooping(isLooping);
    }

    public void start()//开始播放
    {
        mp.start();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void pause()//暂停
    {
        mp.pause();
    }

    public void seekto(int Progress)//播放指定位置
    {
        mp.seekTo(Progress);
    }

    public void play(String url, String title, String artist) {//播放
        isCompleted = false;
        try {
            mp.reset();
            mp.setDataSource(url);
            mp.prepare();
            mp.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("Media", "is playing!!!!!!!!!!!!!!!!!!!!!!!");
    }
    public ArrayList<HashMap<String, Object>> getData(Context context)
    {
        GetList getList=new GetList(context);
        data=getList.getSys();
        return data;
    }
    public int getSongIndex() {
        return songindex;
    }

    public int getLastSongIndex() {
        return lastsongindex;
    }

    public int getIsRandom() {
        return isRandom;
    }

    public boolean getIsChanging() {
        return isChanging;
    }

    public void setSongIndex(int songindex) {
        this.songindex = songindex;
    }

    public void setLastSongIndex(int lastsongindex) {
        this.lastsongindex = lastsongindex;
    }

    public void setIsRandom(int isRandom) {
        this.isRandom = isRandom;
    }

    public void setIsChanging(boolean isChanging) {
        this.isChanging = isChanging;
    }

    public void IsRandomPlus()
    {
        isRandom++;
    }

    public void SongIndexPuls()
    {
        songindex++;
    }



}
