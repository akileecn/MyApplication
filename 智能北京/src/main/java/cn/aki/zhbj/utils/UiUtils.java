package cn.aki.zhbj.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/2/29.
 * ui工具类
 */
public class UiUtils {
    /**
     * px转dp
     */
    public static int px2dp(Context context,float pxValue){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/density+0.5F);
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context,float dpValue){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*density+0.5F);
    }

    /**
     * 获得屏幕大小
     */
    public static Point getWindowSize(Context context){
        Point point=new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        return point;
    }

}
