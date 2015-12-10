package cn.aki.mobilesafe.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/12/4.
 * 服务状态
 */
public class ServiceStatusUtils {
    /**
     * 检测服务是否激活
     */
    public static boolean isActive(Context context,Class<? extends Service> serviceClass){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services=activityManager.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo serviceInfo:services){
            String className=serviceInfo.service.getClassName();
            if(className.equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }

}
