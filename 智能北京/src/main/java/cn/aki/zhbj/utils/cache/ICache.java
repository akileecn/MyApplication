package cn.aki.zhbj.utils.cache;

/**
 * Created by Administrator on 2016/2/25.
 * 缓存接口
 */
public interface ICache<T> {
    /**
     * 保存缓存
     */
    void saveCache(String key,T t);

    /**
     * 获得缓存
     */
    T getCache(String key);
}
