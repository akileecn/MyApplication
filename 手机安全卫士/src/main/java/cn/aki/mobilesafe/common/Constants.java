package cn.aki.mobilesafe.common;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/11/10.
 * 常量
 */
public final class Constants {
    public static final String TAG="akiTag";
    /**
     * 域名
     */
    public static final String BASE_URL="http://192.168.2.37:8080/webTest/";
    /**
     * 更新信息
     */
    public static final String UPDATE_URL=BASE_URL+"update.json";

    /**
     * action常量
     */
    public static final class Action{
        private static final String BASE="cn.aki.mobilesafe.action.";
        /**
         * 打开首页
         */
        public static final String HOME=BASE+"HOME";
        /**
         * 启动清理进程任务
         */
        public static final String KILL_PROGRESS=BASE+"KILL_PROGRESS";
    }

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
        public static final String KEY_SAFE_GUIDED="safeGuided";
        /**
         * sim卡序列号
         */
        public static final String KEY_SIM="sim";
        /**
         * 安全手机
         */
        public static final String KEY_SAFE_PHONE="safePhone";
        /**
         * 是否开启保护
         */
        public static final String KEY_PROTECT = "protect";
        /**
         * 定位
         */
        public static final String KEY_LOCATION="location";
        /**
         * 电话归属地样式
         */
        public static final String KEY_ADDRESS_STYLE="addressStyle";
        /**
         * 归属地x坐标
         */
        public static final String KEY_ADDRESS_LOCATION_X="addressLocationX";
        /**
         * 归属地y坐标
         */
        public static final String KEY_ADDRESS_LOCATION_Y="addressLocationY";
        /**
         * 快捷方式是否存在
         */
        public static final String KEY_SHORTCUT_EXISTS="shortcutExists";
        /**
         * 显示系统进程
         */
        public static final String KEY_SHOW_SYSTEM_PROGRESS="showSystemProgress";
        /**
         * 清理内存定时任务时间间隔
         */
        public static final String KEY_TASK_INTERVAL="taskInterval";

    }

    /**
     * 短信指令
     */
    public static final class SmsCommand{
        /**
         * GPS追踪
         */
        public static final String LOCATION="#*location*#";
        /**
         * 播放报警音乐
         */
        public static final String ALARM="#*alarm*#";
        /**
         * 删除数据
         */
        public static final String WIPE_DATA="#*wipedata*#";
        /**
         * 锁屏
         */
        public static final String LOCK_SCREEN="#*lockscreen*#";
    }

    /**
     * 数据库
     */
    public static final class DataBase{
        /**
         * 归属地
         */
        public static final String Address="data/data/cn.aki.mobilesafe/files/address.db";
        /**
         * 杀毒
         */
        public static final String AntiVirus="data/data/cn.aki.mobilesafe/files/antivirus.db";
    }

    /**
     * 归属地样式
     */
    public static final class AddressStyle{
        /**
         * 描述
         */
        public static final String[] DESC={"蓝","灰","绿","橙","白"};
        /**
         * 图片
         */
        public static final int[] DRAWABLE={R.drawable.call_locate_blue,R.drawable.call_locate_gray
                ,R.drawable.call_locate_green,R.drawable.call_locate_orange,R.drawable.call_locate_white};
    }

    /**
     * 清理内存任务时间间隔
     */
    public static final class TaskInterval{
        /**
         * 描述
         */
        public static final String[] DESC={"锁屏时自动清理","2小时","4小时","6小时"};
        /**
         * 时间间隔索引-锁屏时清理
         */
        public static final int WHICH_LOCK_SCREEN=0;
        /**
         * 时间间隔
         */
        public static final int[] INTERVAL={-1,2,4,6};
    }
}
