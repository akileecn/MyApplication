package cn.aki.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import cn.aki.mobilesafe.activity.AppLockPasswordActivity;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.db.dao.AppLockDao;

/**
 * Created by Administrator on 2016/1/27.
 * 看门狗服务
 */
public class AppLockService extends Service{
    public static final String EXTRA_PACKAGE_NAME="packageName";
    private boolean isRunning;
    private String mExcludePackageName;
    private BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播接收者
        mReceiver=new MyBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        //监听解锁应用
        filter.addAction(Constants.Action.APP_LOCK_EXCLUDE);
        //监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);
        //启动看门狗
        startWatchdog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止看门狗
        isRunning=false;
        //注销接收者
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
    }

    /**
     * 启动看门狗
     */
    private void startWatchdog(){
        final ActivityManager am= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final AppLockDao dao=new AppLockDao(this);
        isRunning=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
                    //获得栈首的应用
                    ActivityManager.RunningTaskInfo taskInfo=taskInfoList.get(0);
                    String packageName=taskInfo.topActivity.getPackageName();
                    Log.d(Constants.TAG, "AppLockService.startWatchdog packageName:" + packageName);
                    if(!packageName.equals(mExcludePackageName)&&dao.exists(packageName)){
                        Intent intent=new Intent(AppLockService.this, AppLockPasswordActivity.class);
                        //拦截的包名传给activity
                        intent.putExtra(EXTRA_PACKAGE_NAME,packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    SystemClock.sleep(1000);
                }
            }
        }).start();
    }

    /**
     * 用户接收临时不屏蔽的包名
     */
    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case Constants.Action.APP_LOCK_EXCLUDE:
                    mExcludePackageName=intent.getStringExtra(EXTRA_PACKAGE_NAME);
                    break;
                //锁屏时停止
                case Intent.ACTION_SCREEN_OFF:
                    isRunning=false;
                    break;
                //解锁屏幕时再次启动
                case Intent.ACTION_SCREEN_ON:
                    startWatchdog();
                    break;
                default:
                    break;
            }
        }
    }
}
