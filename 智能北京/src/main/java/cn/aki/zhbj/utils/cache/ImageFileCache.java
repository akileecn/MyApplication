package cn.aki.zhbj.utils.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/2/25.
 * 图片本地文件缓存
 */
public class ImageFileCache extends FileCache<Bitmap>{
    private static final String TAG= ImageFileCache.class.getName();

    public ImageFileCache(Context context) {
        super(context);
    }

    @Override
    public void saveCache(String key, Bitmap bitmap) {
        File file=getCacheFile(key);
        FileOutputStream os=null;
        try {
            os=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Bitmap getCache(String key) {
        return getCache(key,null);
    }

    public Bitmap getCache(String key,BitmapFactory.Options options) {
        File file=getCacheFile(key);
        if(file!=null&&file.exists()) {
            Log.d(TAG, "getCache->" + key);
            if(options==null){
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }else{
                return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            }
        }
        return null;
    }
}
