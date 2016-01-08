package cn.aki.mobilesafe.utils;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/8.
 * UI工具类
 */
public class UiUtils {
    /**
     * 吐司
     * @param activity context
     * @param message 内容
     */
    public static void showToast(final Activity activity, final String message){
        //判断是否为主线程
        if(Looper.myLooper()==Looper.getMainLooper()){
            Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
        }else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
