package cn.aki.mobilesafe.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.receiver.ProgressAppWidgetProvider;
import cn.aki.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by Administrator on 2016/1/15.
 * 进程widget服务
 */
public class ProgressAppWidgetService extends Service{
    private Timer mTimer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG, "ProgressAppWidgetService onCreate");
        //widget管理器
        final AppWidgetManager widgetManager=AppWidgetManager.getInstance(this);
        //远程视图
        final RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.appwidget_progress);
        //widgetProvider控件
        final ComponentName componentName=new ComponentName(ProgressAppWidgetService.this,ProgressAppWidgetProvider.class);
        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(Constants.TAG, "ProgressAppWidgetService onCreate:定时更新widget");
                //活动进程
                int progressCount= SystemInfoUtils.getRunningProgressCount(ProgressAppWidgetService.this);
                //可用内存
                long availMem=SystemInfoUtils.getMemoryInfo(ProgressAppWidgetService.this)[0];
                remoteViews.setTextViewText(R.id.tv_process_count,"运行中的进程"+progressCount+"个");
                remoteViews.setTextViewText(R.id.tv_process_memory, "剩余可用内存" + Formatter.formatFileSize(ProgressAppWidgetService.this, availMem));
                widgetManager.updateAppWidget(componentName,remoteViews);
            }
        },0,1000*60*10);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }
}
