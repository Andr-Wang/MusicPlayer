package com.example.wangtao.mymusicplayer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.IBinder;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private TimerTask mTimerTask;
    private Timer mTimer;
    private SeekBar skbr;
    private ImageButton play,next,state;
    private ImageView Img;
    Random random = new Random();
    ListView list;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
    private MyServiceConn conn;
    private MusicService service;
    MusicAdapter musicAdapter;
    private boolean isDownloadLyrics=false;
    TextView ti,ar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置activity notitle
        setContentView(R.layout.activity_main);



//        lrc=new SearchLRC("黄昏","周传雄");
//        ArrayList gcContent;
//        gcContent=lrc.fetchLyric();
//        Log.e("start to show","sssssssssssssssssssssssssssssssssss");
//        if(gcContent.size()==0)
//            Log.e("Size ======0","Size=============================================00000000000000");
//        for (int i=0;i<gcContent.size();i++)
//        {
//            Log.e("!!!!!!!!!!!!!!!!!!!!!", gcContent.get(i).toString());
//        }
        findview();

        setlistener();

        setImg();
    }

    protected void onStop() {
        Log.e("onStop", "");
        super.onStop();
    }

    @Override
    protected void onResume() {

        conn=new MyServiceConn();
        bindService(new Intent(MainActivity.this, MusicService.class), conn, BIND_AUTO_CREATE);

        Log.e("onResume", "onResume");

        super.onResume();
    }

    public void setImg()
    {
        state.setBackgroundResource(R.drawable.circle);
        play.setBackgroundResource(R.drawable.play);
        next.setBackgroundResource(R.drawable.next);
        Img.setBackgroundResource(R.drawable.music);
    }

    private void setView()
    {

        switch (service.getIsRandom() % 3) {
            case 0:
                state.setBackgroundResource(R.drawable.circle);
                Toast.makeText(MainActivity.this, "play around the list", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                state.setBackgroundResource(R.drawable.random);
                Toast.makeText(MainActivity.this, "switch to random play model", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                state.setBackgroundResource(R.drawable.one);
                Toast.makeText(MainActivity.this, "switch to one song model", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        if (service.isPlaying() == true) {
            play.setBackgroundResource(R.drawable.pause);
        } else {
            play.setBackgroundResource(R.drawable.play);
        }
    }
    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MusicService.LocalBinder) binder).getService();
            //将当前activity添加到接口集合中
            //service.addCallback(MainActivity.this);
            setView();
            getSys();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            service = null;
        }
    }
    private void findview()
    {
        play =(ImageButton) findViewById(R.id.play);
        state=(ImageButton) findViewById(R.id.state);
        list = (ListView) findViewById(R.id.listview);
        next=(ImageButton) findViewById(R.id.next);
        skbr = (SeekBar) findViewById(R.id.skbr1);
        Img=(ImageView)findViewById(R.id.picture);//专辑图片
        ti = (TextView) findViewById(R.id.title);
        ar = (TextView) findViewById(R.id.artist);
    }

    private void skip(int position)//跳转到第二个界面
    {
        Intent intent=new Intent(MainActivity.this,Main2Activity.class);
        Bundle bundle=new Bundle();
        bundle.putString("title", data.get(position).get("title").toString());
        bundle.putString("artist", data.get(position).get("artist").toString());
        bundle.putString("itemPosition", String.valueOf(position));//歌曲是第几首
        bundle.putString("itemSize", String.valueOf(data.size()));//歌曲总数
        bundle.putString("rate", data.get(position).get("rate").toString());
        bundle.putString("currentPosition", String.valueOf(service.getCurrentPosition()));
        bundle.putString("duration",String.valueOf(service.getDuration()));
        if (isDownloadLyrics)
        {
            bundle.putBoolean("DownloadLyrics",false);//不用下载

        }else
        {
            bundle.putBoolean("DownloadLyrics",true);//需要下载
            isDownloadLyrics=true;//设置为已经下载
        }
        intent.putExtra("MusicInfo",bundle);
        startActivity(intent);
    }
    private void setlistener()//设置各监听
    {
        Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (service.getSongIndex() != -1) {
                    skip(service.getSongIndex());
                }
            }
        });
        skbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                service.setIsChanging(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                service.seekto(seekBar.getProgress());
                service.setIsChanging(false);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == service.getSongIndex())//当前正在播放此歌曲
                {
                    skip(position);
                } else {
                    int s = service.getSongIndex();
                    service.setLastSongIndex(s);
                    service.setSongIndex(position);
                    play();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//循环图标
                service.IsRandomPlus();
                switch (service.getIsRandom() % 3) {
                    case 0:
                        state.setBackgroundResource(R.drawable.circle);
                        Toast.makeText(MainActivity.this, "play around the list", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        state.setBackgroundResource(R.drawable.random);
                        Toast.makeText(MainActivity.this, "switch to random play model", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        state.setBackgroundResource(R.drawable.one);
                        Toast.makeText(MainActivity.this, "switch to one song model", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//播放按钮
                if (service.isPlaying() == true) {
                    play.setBackgroundResource(R.drawable.play);
                    service.pause();
                } else {
                    play.setBackgroundResource(R.drawable.pause);
                    service.start();
                }
            }
        });
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(service==null)
                {
                    Log.e("Binder","Binder=null!!!!!!!!!!!!!!!");
                    return;
                }
                if (service.getIsChanging() == true)
                {
                    return;
                }
                if(service.isPlaying())
                {
                    skbr.setProgress(service.getCurrentPosition());
                }else
                {
                    if(service.isCompleted()==true)//播放完成
                    {
                        runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                play();
                            }
                        });

                    }
                }

            }
        };
        mTimer.schedule(mTimerTask, 0, 600);

    }

    private void next()
    {
        service.next();
        isDownloadLyrics=false;//设置为没有下载
        Log.e("songindex=", String.valueOf(service.getSongIndex()) + "  " + String.valueOf(service.getLastSongIndex()));
        setVisible();
        play.setBackgroundResource(R.drawable.pause);
        setinfo(data.get(service.getSongIndex()).get("title").toString(), data.get(service.getSongIndex()).get("artist").toString(), data.get(service.getSongIndex()).get("songid").toString());
        skbr.setMax(service.getDuration());
    }
    private void play()
    {


        isDownloadLyrics=false;//设置为没有下载
        Log.e("songindex=", String.valueOf(service.getSongIndex()) + "  " + String.valueOf(service.getLastSongIndex()));
        setVisible();
        service.play(data.get(service.getSongIndex()).get("url").toString(), data.get(service.getSongIndex()).get("title").toString(), data.get(service.getSongIndex()).get("artist").toString());
        play.setBackgroundResource(R.drawable.pause);
        setinfo(data.get(service.getSongIndex()).get("title").toString(), data.get(service.getSongIndex()).get("artist").toString(), data.get(service.getSongIndex()).get("songid").toString());
        skbr.setMax(service.getDuration());
    }
    private void setVisible()//设置播放标志可见
    {


        if(service.getLastSongIndex()!=-1)
        {
            Log.e("setVisible","getLastSongIndex!=-1");
            HashMap<String, Object> lmap = new HashMap<String, Object>();
            lmap.put("title", data.get(service.getLastSongIndex()).get("title"));
            lmap.put("artist", data.get(service.getLastSongIndex()).get("artist"));
            lmap.put("url", data.get(service.getLastSongIndex()).get("url"));
            lmap.put("rate", data.get(service.getLastSongIndex()).get("rate"));
            lmap.put("songid", data.get(service.getLastSongIndex()).get("songid"));
            lmap.put("isplay","0");
            data.set(service.getLastSongIndex(), lmap);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", data.get(service.getSongIndex()).get("title"));
        map.put("artist", data.get(service.getSongIndex()).get("artist"));
        map.put("url", data.get(service.getSongIndex()).get("url"));
        map.put("rate", data.get(service.getSongIndex()).get("rate"));
        map.put("songid", data.get(service.getSongIndex()).get("songid"));
        map.put("isplay", "1");

        data.set(service.getSongIndex(), map);
        musicAdapter.notifyDataSetChanged();
    }

    public void getSys()//从系统数据库获取播放列表
    {

        data=service.getData(this);
        musicAdapter=new MusicAdapter(this,data);
        list.setAdapter(musicAdapter);
    }
    private void setinfo(String title, String artist,String songid) {//显示歌曲信息

        ti.setText(title);
        ar.setText(artist);
//        Bitmap bitmap=setAlbumImg(MainActivity.this, songid);
//
//        if (bitmap==null)
//        {
//            Log.e("setAlbumImg.songid","null");
//            Img.setBackgroundResource(R.drawable.music);
//        }else
//        {
//            Log.e("setAlbumImg.size= ", String.valueOf(bitmap.getByteCount()));
//            Img.setImageBitmap(bitmap);
//        }
    }


    @Override
    protected void onDestroy()
    {
        unbindService(conn);

        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//返回键防退出
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}