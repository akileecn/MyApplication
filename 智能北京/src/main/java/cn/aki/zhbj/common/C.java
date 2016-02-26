package cn.aki.zhbj.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/2/2.
 * 常量
 */
public final class C {
    /**
     * 获得配置SharedPreferences
     */
    public static SharedPreferences getConfig(Context context){
        return context.getSharedPreferences(Sp.FILE_CONFIG,Context.MODE_PRIVATE);
    }
    /**
     * SharedPreferences
     */
    public interface Sp{
        String FILE_CONFIG="config";
        /**是否完成引导页*/
        String KEY_IS_GUIDED="isGuided";
        /**网页字体大小*/
        String KEY_WEB_TEXT_SIZE="webTextSize";
        /**点击的链接*/
        String KEY_CLICKED_LINK="clickedLink";
        /**组图显示类型*/
        String KEY_PHOTO_SHOW_TYPE="photoShowType";
    }

    public interface Url{
        String BASE="http://192.168.2.37:8080/zhbj/";
        String DEPRECATED_BASE="http://10.0.2.2:8080/zhbj/";
        String CATEGORIES=BASE+"categories.json";
        String PHOTO=BASE+"photos/photos_1.json";
    }

    public interface Extra{
        String URL="url";
    }

    public interface Key{
        /**百度云推送apiKey*/
        String BAIDU_PUSH_API_KEY="oIvGEaTk7VQs0GoIUf05AWG9";
    }
}
