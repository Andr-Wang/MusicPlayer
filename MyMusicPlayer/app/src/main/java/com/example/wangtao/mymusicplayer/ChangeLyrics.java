package com.example.wangtao.mymusicplayer;

import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangtao on 2015/12/10.
 */
public class ChangeLyrics
{
    ArrayList gcContent =new ArrayList();
    ArrayList time=new ArrayList();
    ArrayList content=new ArrayList();
    public void setContent( ArrayList gcContent)
    {
        this.gcContent=gcContent;
    }

    public void change()
    {
        for(int i=0;i<gcContent.size()-1;i++)
        {
            String data=gcContent.get(i).toString();
            int currTime=0;
            String lrcContenet = null;
            Pattern pattern = Pattern.compile("\\d{2}");
            data = data.replace("[","");//将前面的替换成后面的
            data = data.replace("]","@");
            String splitdata[] =data.split("@");//分隔
            lrcContenet=" ";
            if(data.endsWith("@")){
                for(int k=0;k<splitdata.length;k++){
                    String str=splitdata[k];
                    str = str.replace(":",".");
                    str = str.replace(".","@");
                    String timedata[] =str.split("@");
                    Matcher matcher = pattern.matcher(timedata[0]);
                    if(timedata.length==3 && matcher.matches()){
                        int m = Integer.parseInt(timedata[0]);  //分
                        int s = Integer.parseInt(timedata[1]);  //秒
                        int ms = Integer.parseInt(timedata[2]); //毫秒
                        currTime = (m*60+s)*1000+ms*10;

                    }
                }
            }
            else{
                lrcContenet = splitdata[splitdata.length-1];
                for (int j=0;j<splitdata.length-1;j++)
                {
                    String tmpstr = splitdata[j];
                    tmpstr = tmpstr.replace(":",".");
                    tmpstr = tmpstr.replace(".","@");
                    String timedata[] =tmpstr.split("@");
                    Matcher matcher = pattern.matcher(timedata[0]);
                    if(timedata.length==3 && matcher.matches()){
                        int m = Integer.parseInt(timedata[0]);  //分
                        int s = Integer.parseInt(timedata[1]);  //秒
                        int ms = Integer.parseInt(timedata[2]); //毫秒
                        currTime = (m*60+s)*1000+ms*10;
                    }
                }
                time.add(currTime);
                content.add(lrcContenet);
            }

        }


    }
    public ArrayList getTime()
    {
        return time;
    }

    public ArrayList getContent()
    {
        return content;
    }
}
