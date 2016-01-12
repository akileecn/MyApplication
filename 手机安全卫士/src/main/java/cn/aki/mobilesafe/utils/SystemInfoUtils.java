package cn.aki.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/12.
 *  系统信息工具类
 */
public class SystemInfoUtils {
    /**
     * 获得内存信息
     * @return {总内存,可用内存}
     */
    public static long[] getMemoryInfo(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
//        return new long[]{mi.availMem,mi.totalMem};
        return new long[]{mi.availMem,getTotalMem()};
    }

    /**
     * 读取内存信息文件获取总内存
     * @return 总内存
     */
    private static long getTotalMem(){
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(new File("/proc/meminfo")));
            String line;
            while((line=br.readLine())!=null){
                if(line.contains("MemTotal:")){
                    Pattern p=Pattern.compile("(?<=\\s)\\d+");
                    Matcher m=p.matcher(line);
                    if(m.find()){
                        return Long.parseLong(m.group(0))<<10;
                    }
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
