package cn.aki.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.manager.ProgressInfoManager;

/**
 * Created by Administrator on 2016/1/13.
 * 清理进程任务
 */
public class KillProgressService extends Service{
    private Timer mTimer;
    private BroadcastReceiver mReceiver;
    private ProgressInfoManager mProgressInfoManager;
    private int times=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mProgressInfoManager=new ProgressInfoManager(this);
        SharedPreferences pref = getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        int which= pref.getInt(Constants.SharedPreferences.KEY_TASK_INTERVAL, 0);
        if(which==Constants.TaskInterval.WHICH_LOCK_SCREEN){
            //注册锁屏广播接收器
            mReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    killProgress();
                }
            };
            IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mReceiver,filter);
        }else{
            long period=Constants.TaskInterval.INTERVAL[which]*1000;
            mTimer=new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    killProgress();
                }
            },0,period);
        }

    }

    private void killProgress(){
        times++;
        System.err.println(">>>>>>>>>>>>>>start kill progress "+times);
        mProgressInfoManager.killOthers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
    }
}
