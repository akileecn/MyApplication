package cn.aki.zhnbj.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/2/2.
 * 常量
 */
public final class C {
    /**
     * SharedPreferences
     */
    public static final class Sp{
        private static final String FILE_NAME="config";

        /**
         * 获得SharedPreferences
         */
        public static SharedPreferences getSharedPreferences(Context context){
            return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        }
        /**是否完成引导页*/
        public static final String KEY_IS_GUIDED="isGuided";
    }
}
