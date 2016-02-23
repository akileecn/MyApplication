package cn.aki.zhbj.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/2/23.
 * 缓存工具类
 */
public class CacheUtils {
    /**
     * 保存缓存
     */
    public static void saveCache(Context context,String url,String data){
        if(TextUtils.isEmpty(url)||TextUtils.isEmpty(data)){
            return;
        }
        String fileName=Md5Utils.encode(url);
        // /mnt/sdcard/Android/data/cn.aki.zhbj/cache
        File cacheFile=new File(context.getExternalCacheDir(),fileName);
        if(!cacheFile.exists()){
            FileUtils.string2file(data,cacheFile);
        }
    }

    /**
     * 读取缓存
     */
    public static String getCache(Context context,String url){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        String fileName=Md5Utils.encode(url);
        File cacheFile=new File(context.getExternalCacheDir(),fileName);
        if(cacheFile.exists()){
            return FileUtils.file2string(cacheFile);
        }else{
            return null;
        }
    }
}
