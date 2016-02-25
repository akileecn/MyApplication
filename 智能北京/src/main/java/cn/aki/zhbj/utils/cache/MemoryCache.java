package cn.aki.zhbj.utils.cache;

import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by Administrator on 2016/2/25.
 * 内存缓存工具
 */
public abstract class MemoryCache<T> implements ICache<T>{
    private static final String TAG=MemoryCache.class.getName();
    protected LruCache<String,T> mLruCache;

    public MemoryCache() {
        int size= (int) (Runtime.getRuntime().maxMemory()/8);
        mLruCache=new LruCache<String,T>(size){
            @Override
            protected int sizeOf(String key, T value) {
                return sizeOfValue(value);
            }
        };
    }

    public void saveCache(String key,T t) {
        mLruCache.put(key,t);
        Log.d(TAG, "saveCache->" + t);
    }

    public T getCache(String key) {
        T t=mLruCache.get(key);
        if(t!=null){
            Log.d(TAG, "getCache->" + key);
        }
        return t;
    }

    /**
     * 计算对象大小
     */
    protected abstract int sizeOfValue(T t);

}
