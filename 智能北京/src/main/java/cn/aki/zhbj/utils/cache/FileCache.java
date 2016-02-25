package cn.aki.zhbj.utils.cache;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import cn.aki.zhbj.utils.Md5Utils;

/**
 * Created by Administrator on 2016/2/25.
 * 文件缓存
 */
public abstract class FileCache<T> implements ICache<T>{
    private static final String TAG= FileCache.class.getName();
    private String mCachePath;

    public FileCache(Context context){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            mCachePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+context.getPackageName()+"/cache";
            File cacheDir=new File(mCachePath);
            //不存在或创建失败
            if(!cacheDir.exists()&&!cacheDir.mkdirs()){
                mCachePath=context.getCacheDir().getAbsolutePath();
            }
        }else{
            mCachePath=context.getCacheDir().getAbsolutePath();
        }
        Log.d(TAG, "cachePath->" + mCachePath);
    }

    /**
     * 获取对应的缓存文件
     */
    public File getCacheFile(String key){
        key = Md5Utils.encode(key);
        if(TextUtils.isEmpty(key)){
            return null;
        }else {
            return new File(mCachePath, key);
        }
    }

}
