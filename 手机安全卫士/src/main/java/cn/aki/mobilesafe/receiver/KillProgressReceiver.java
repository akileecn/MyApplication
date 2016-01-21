package cn.aki.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.manager.ProgressInfoManager;
import cn.aki.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by Administrator on 2016/1/15.
 * 清理进程(用于响应widget的按钮)
 */
public class KillProgressReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.TAG, "KillProgressReceiver onReceive");
        /**清理进程*/
        ProgressInfoManager progressInfoManager=new ProgressInfoManager(context);
        progressInfoManager.killOthers();
        /**刷新界面*/
        AppWidgetManager widgetManager=AppWidgetManager.getInstance(context);
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.appwidget_progress);
        ComponentName componentName=new ComponentName(context,ProgressAppWidgetProvider.class);
        Log.d(Constants.TAG, "KillProgressReceiver onReceive:更新widget");
        //活动进程
        int progressCount= SystemInfoUtils.getRunningProgressCount(context);
        //可用内存
        long availMem=SystemInfoUtils.getMemoryInfo(context)[0];
        remoteViews.setTextViewText(R.id.tv_process_count,"运行中的进程"+progressCount+"个");
        remoteViews.setTextViewText(R.id.tv_process_memory, "剩余可用内存" + Formatter.formatFileSize(context, availMem));
        widgetManager.updateAppWidget(componentName,remoteViews);
    }
}
