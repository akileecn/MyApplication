package cn.aki.mobilesafe.common;

/**
 * Created by Administrator on 2015/11/10.
 * 常量
 */
public final class Constants {
    /**
     * 域名
     */
    public static final String BASE_URL="http://192.168.2.37:8080/webTest/";
    /**
     * 更新信息
     */
    public static final String UPDATE_URL=BASE_URL+"update.json";

    /**
     * 参数
     */
    public static final class SharedPreferences{
        public static final String FILE_CONFIG="config";
        /**
         * 自动更新key
         */
        public static final String KEY_AUTO_UPDATE="autoUpdate";
        /**
         * 密码
         */
        public static final String KEY_PASSWORD="password";
        /**
         * 完成设置向导
         */
        public static final String KEY_GUIDED="guided";

    }
}
