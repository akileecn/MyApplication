package cn.aki.zhbj.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import cn.aki.zhbj.utils.cache.ICache;
import cn.aki.zhbj.utils.cache.ImageFileCache;
import cn.aki.zhbj.utils.cache.ImageMemoryCache;
import cn.aki.zhbj.utils.http.BitmapHttpTask;

/**
 * Created by Administrator on 2016/2/24.
 * 图片工具类
 */
public class ImageUtils {
    private static final String TAG=ImageUtils.class.getName();
    private ICache<Bitmap> mFileCache;
    private ICache<Bitmap> mMemoryCache;

    public ImageUtils(Context context){
        mFileCache=new ImageFileCache(context);
        mMemoryCache=new ImageMemoryCache();
    }

    /**
     * 绑定图片
     */
    public void bind(String url,ImageView imageView){
        Log.d(TAG, "bind->url:" + url);
        //从内存中获得
        Bitmap bitmap=null;
//        Bitmap bitmap=mMemoryCache.getCache(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else{
            //从本地文件中获得
            bitmap=mFileCache.getCache(url);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
                //保存内存缓存
                mMemoryCache.saveCache(url, bitmap);
            }else{
                //从网络获得
                new LoadingImageTask().execute(url, imageView);
            }
        }
    }

    /**
     * 加载图片
     */
    private class LoadingImageTask extends AsyncTask<Object,Integer,Bitmap>{
        private ImageView imageView;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String url= (String) params[0];
            imageView= (ImageView) params[1];
            //网络缓存
            Bitmap bitmap=new BitmapHttpTask(url).execute();
            if(bitmap!=null){
                //文件缓存
                mFileCache.saveCache(url,bitmap);
                //内存缓存
                mMemoryCache.saveCache(url,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}
