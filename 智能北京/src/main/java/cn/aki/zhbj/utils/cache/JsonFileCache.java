package cn.aki.zhbj.utils.cache;

import android.content.Context;

import java.io.File;

import cn.aki.zhbj.utils.FileUtils;

/**
 * Created by Administrator on 2016/2/23.
 * 缓存工具类
 */
public class JsonFileCache extends FileCache<String> {

    public JsonFileCache(Context context) {
        super(context);
    }

    @Override
    public void saveCache(String url, String data) {
        File cacheFile=getCacheFile(url);
        if(cacheFile!=null&&!cacheFile.exists()){
            FileUtils.string2file(data, cacheFile);
        }
    }

    @Override
    public String getCache(String url) {
        File cacheFile=getCacheFile(url);
        if(cacheFile!=null&&cacheFile.exists()){
            return FileUtils.file2string(cacheFile);
        }else{
            return null;
        }
    }
}
