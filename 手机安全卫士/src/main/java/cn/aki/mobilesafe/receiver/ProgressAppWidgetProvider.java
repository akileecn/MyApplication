package cn.aki.mobilesafe.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.service.ProgressAppWidgetService;

/**
 * Created by Administrator on 2016/1/15.
 * 进程窗口小部件
 */
public class ProgressAppWidgetProvider extends AppWidgetProvider{
    /**
     * 创建新的实例(主要被重写方法)
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //更新所有创建的widget
        for(int appWidgetId:appWidgetIds){
            Intent intent=new Intent();
            intent.setAction(Constants.Action.KILL_PROGRESS);
            //发送广播
            PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, 0);
            //在视图上绑定点击事件
            RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.appwidget_progress);
            rv.setOnClickPendingIntent(R.id.btn_clear,pi);
            //更新widget
            appWidgetManager.updateAppWidget(appWidgetId,rv);
        }
    }

    /**
     * 移除一个实例
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 首次创建或全部被删除后首次创建
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //开启定时清理进程的任务
        context.startService(new Intent(context, ProgressAppWidgetService.class));
    }

    /**
     * 最后一个实例被移除
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //关闭服务
        context.stopService(new Intent(context,ProgressAppWidgetService.class));
    }
}
