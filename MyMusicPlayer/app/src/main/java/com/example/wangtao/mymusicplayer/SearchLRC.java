package com.example.wangtao.mymusicplayer;

/**
 * Created by wangtao on 2015/11/21.
 */

import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLEncoder;
        import java.util.ArrayList;



import android.util.Log;
public class SearchLRC{


    // number=0表示暂无歌词
    String name,singer;
    int num;

    public int getNumber(String musicName, String singerName)
    {
        try {
            name = URLEncoder.encode(musicName, "utf-8");//编码
            singer = URLEncoder.encode(singerName, "utf-8");
        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

                URL url = null;
                StringBuffer sb = new StringBuffer();
                int number=0;
                String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + name + "$$"+ singer +"$$$$";
                Log.d("test", strUrl);
                try {
                    url = new URL(strUrl);
                    Log.d("SearchLRC==============","url = "+url);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                BufferedReader br = null;
                try {
                    HttpURLConnection httpConn   =   (HttpURLConnection)url.openConnection();
                    httpConn.setConnectTimeout(5000);
                    httpConn.connect();
                    httpConn.connect();
                    InputStreamReader inReader = new InputStreamReader(httpConn.getInputStream());

                    Log.d("the encode is ", inReader.getEncoding());
                    br = new BufferedReader(inReader);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.d("tag", "br is null");
                }
                try {
                    String s;
                    while ((s = br.readLine()) != null) {
                        Log.d("SearchLRC","s = "+s);
                        sb.append(s + "/r/n");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }finally{
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                }
                int begin = sb.indexOf("<lrcid>");
                Log.e("test", "sb = " + sb);
                if (begin != -1) {
                    int end = sb.indexOf("</lrcid>", begin);
                    String strid = sb.substring(begin + 7, end);
                    number = Integer.parseInt(strid);
                }

        return number;
    }

    public ArrayList fetchLyric(int number)
    {
        num=number;

                URL url = null;
                ArrayList gcContent =new ArrayList();
                String geciURL = "http://box.zhangmen.baidu.com/bdlrc/" + num / 100 + "/" + num + ".lrc";
               // Log.e("test!!!!!!!!!!!!!!!!!!!", String.valueOf(number));
                Log.e("test", "geciURL = " + geciURL);

                String s = new String();
                try {
                    url = new URL(geciURL);
                } catch (MalformedURLException e2) {
                    e2.printStackTrace();
                }
                BufferedReader br1 = null;
                try {
                    br1 = new BufferedReader(new InputStreamReader(url.openStream(), "GB2312"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (br1 == null) {
                    System.out.print("stream is null");
                } else {
                    try {
                        while ((s = br1.readLine()) != null) {
                            gcContent.add(s);
                        }
                        br1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                Looper.prepare();
//                Handler handler=new Handler();
//                Message message=new Message();
//                Bundle bundle=new Bundle();
//                message.what = 2;
//                bundle.putStringArrayList("Lyrics", gcContent);
//                message.setData(bundle);
//                handler.sendMessage(message);

      return gcContent;
    }


}
