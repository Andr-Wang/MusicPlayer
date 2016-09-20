package com.example.wangtao.mymusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Sampler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

public class Main2Activity extends Activity {
    private MyServiceConn conn;
    private MusicService service;
    TextView ti, ar,itemC,currentP,size,rate;
    private SeekBar skbr;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private ListView listView;
    private String title,artist;
    private static ArrayList LyricsContent,LyricsTime;
    private static boolean isActive=false,hasLyrics=false;
    ListAdapter listAdapter;
    private ImageView goback,playstate,playm,nextm,backm;
    Handler handler=new Handler()//消息接受
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            switch(msg.what) {
                case 1:

                    Log.e("handle= ","msg=1");
                    Log.e("bundle= ", String.valueOf(bundle.getStringArrayList("Lyrics").size()));

                    setLyAdapter(bundle.getStringArrayList("Lyrics"), bundle.getStringArrayList("Time"));
                    hasLyrics=true;
                    break;
                case 2:
                    setNoLyrics();
                default:
                    break;
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        conn=new MyServiceConn();
        bindService(new Intent(Main2Activity.this, MusicService.class), conn, BIND_AUTO_CREATE);

        findview();
        getBundle();
        setlistener();
    }

    @Override
    protected void onResume() {
        isActive=true;
        super.onResume();

    }

    private void setLyAdapter(ArrayList contentList,ArrayList timeList)
    {
        LyricsContent=new ArrayList();
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        for(int i=0;i<contentList.size();i++)
        {
            LyricsContent.add(contentList.get(i).toString());
        }
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsContent.add((char)32);
        LyricsTime=timeList;
        listAdapter = new ListAdapter(Main2Activity.this,LyricsContent);
        listView.setAdapter(listAdapter);
    }

    private void LoopLyrics()
    {
        if(LyricsContent==null||LyricsTime==null){return;}
        if(LyricsContent.size()<=1&&!isActive&&!hasLyrics)//没歌词
        {}
        else//有歌词
        {

            for(int i=0;i<LyricsTime.size()-1;i++)
            {
                if(service.getCurrentPosition()<=Integer.valueOf(LyricsTime.get(i+1).toString())&&service.getCurrentPosition()>=Integer.valueOf(LyricsTime.get(i).toString()))
                {
                    Loops(i);//播放至第i行歌词
//                    Log.e("LoopLyrics", String.valueOf(i));
//                    Log.e("LoopLyrics","   "+LyricsTime.get(i).toString()+"   "+service.getCurrentPosition()+"   "+LyricsTime.get(i+1).toString());
                }
            }
        }
    }
    private void Loops(int position) {
        if(isActive&&hasLyrics&&listAdapter != null)
        {
            listView.setSelection(position);
            listAdapter.notifyDataSetInvalidated();
        }
    }

    private void setLyrics(boolean DownloadLyrics)
    {
        if(DownloadLyrics)
        {
            Log.e("setLyrics", "正在下载歌词");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    SearchLRC searchLRC=new SearchLRC();
                    final int i=searchLRC.getNumber(title,artist);
                    if(i==0)//没歌词
                    {
                        Message message=new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }else//有歌词
                    {
                        final ChangeLyrics changeLyrics=new ChangeLyrics();
                        changeLyrics.setContent(searchLRC.fetchLyric(i));
                        changeLyrics.change();
                        Message message=new Message();
                        Bundle bundle=new Bundle();
                        message.what = 1;
                        bundle.putStringArrayList("Lyrics", changeLyrics.getContent());
                        bundle.putStringArrayList("Time",changeLyrics.getTime());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            }).start();

        }else {
            Log.e("setLyrics=", "并没有下载歌词,加载已下载歌词");

            if (LyricsContent != null)
            {
                listAdapter = new ListAdapter(Main2Activity.this,LyricsContent);
                listView.setAdapter(listAdapter);
            }

        }
    }
    private void setNoLyrics()
    {
        hasLyrics=false;
        ArrayList empty=new ArrayList();
        empty.add((char) 32);
        empty.add((char) 32);
        empty.add((char) 32);
        empty.add((char) 32);
        empty.add((char) 32);
        empty.add("未检索到歌词");
        LyricsContent=empty;

        listAdapter = new ListAdapter(Main2Activity.this,empty);
        listView.setAdapter(listAdapter);
    }

    private void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("MusicInfo");
        Log.e("title=", bundle.getString("title"));
        Log.e("artist=", bundle.getString("artist"));
        ti.setText(bundle.getString("title"));
        ar.setText(bundle.getString("artist"));
        if (bundle.getString("currentPosition")!=null)skbr.setProgress(Integer.valueOf(bundle.getString("currentPosition")));
        itemC.setText(bundle.getString("itemPosition")+"/"+bundle.getString("itemSize"));
        currentP.setText(bundle.getString("currentPosition"));
        size.setText(bundle.getString("duration"));
        rate.setText(bundle.getString("rate"));
        title = bundle.getString("title");
        artist = bundle.getString("artist");
        Log.e("getBundle=", String.valueOf(bundle.getBoolean("DownloadLyrics")));

        setLyrics(bundle.getBoolean("DownloadLyrics"));

    }
    private void setlistener()
    {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run()
            {
                if(service!=null)
                {
                    skbr.setProgress(service.getCurrentPosition());
                }
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        LoopLyrics();
                    }
                });
            }
            };

        mTimer.schedule(mTimerTask,0,600);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        playstate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.IsRandomPlus();
                switch (service.getIsRandom() % 3) {
                    case 0:
                        playstate.setBackgroundResource(R.drawable.circle);
                        Toast.makeText(Main2Activity.this, "play around the list", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        playstate.setBackgroundResource(R.drawable.random);
                        Toast.makeText(Main2Activity.this, "switch to random play model", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        playstate.setBackgroundResource(R.drawable.one);
                        Toast.makeText(Main2Activity.this, "switch to one song model", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        playm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (service.isPlaying() == true) {
                    playm.setBackgroundResource(R.drawable.play);
                    service.pause();
                } else {
                    playm.setBackgroundResource(R.drawable.pause);
                    service.start();
                }
            }
        });
        nextm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


            }
        });
        backm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction("LocalBroadCast");
                Bundle bundle=new Bundle();
                bundle.putString("msg","圣骑士wind");
                intent.putExtra("ms", bundle);
                sendBroadcast(intent);
            }
        });
    }
    private void findview()
    {
        ti = (TextView) findViewById(R.id.title2);
        ar = (TextView) findViewById(R.id.artist2);
        skbr = (SeekBar) findViewById(R.id.skbr);
        itemC=(TextView)findViewById(R.id.itemcount);
        currentP=(TextView)findViewById(R.id.currentposition);
        size=(TextView)findViewById(R.id.size);
        rate=(TextView)findViewById(R.id.rate);
        listView=(ListView)findViewById(R.id.listview);
        goback=(ImageView)findViewById(R.id.goback);
        playstate=(ImageView)findViewById(R.id.playstate);
        playm=(ImageView)findViewById(R.id.playm);
        nextm=(ImageView)findViewById(R.id.nextm);
        backm=(ImageView)findViewById(R.id.backm);

    }
    private void setView()
    {
        skbr.setMax(service.getDuration());
        switch (service.getIsRandom() % 3) {
            case 0:
                playstate.setBackgroundResource(R.drawable.circle);
                Toast.makeText(Main2Activity.this, "play around the list", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                playstate.setBackgroundResource(R.drawable.random);
                Toast.makeText(Main2Activity.this, "switch to random play model", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                playstate.setBackgroundResource(R.drawable.one);
                Toast.makeText(Main2Activity.this, "switch to one song model", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        if (service.isPlaying() == true) {
            playm.setBackgroundResource(R.drawable.pause);
        } else {
            playm.setBackgroundResource(R.drawable.play);
        }
    }
    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MusicService.LocalBinder) binder).getService();
            setView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            service = null;
        }
    }

    @Override
    protected void onStop() {
        isActive=false;
        unbindService(conn);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("activity2  ", "onDestroy");

        super.onDestroy();
    }
}