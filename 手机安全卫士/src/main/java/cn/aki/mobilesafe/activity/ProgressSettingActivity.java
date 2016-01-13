package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.service.KillProgressService;
import cn.aki.mobilesafe.utils.ServiceStatusUtils;
import cn.aki.mobilesafe.view.SettingClickView;
import cn.aki.mobilesafe.view.SettingItemView;

/**
 * Created by Administrator on 2016/1/13.
 * 进程管理设置
 */
public class ProgressSettingActivity extends Activity{
    SettingItemView sivShowSystem;
    SettingItemView sivTask;
    SettingClickView scvTaskInterval;
    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI(){
        setContentView(R.layout.activity_progress_setting);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        initShowSystemUI();
        initTaskUI();
        initTaskIntervalUI();
    }

    /**
     * 初始化UI-是否显示系统进程
     */
    private void initShowSystemUI(){
        sivShowSystem= (SettingItemView) findViewById(R.id.siv_show_system);
        sivShowSystem.check(mPref.getBoolean(Constants.SharedPreferences.KEY_SHOW_SYSTEM_PROGRESS, false));
        sivShowSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !sivShowSystem.isChecked();
                sivShowSystem.check(checked);
                mPref.edit().putBoolean(Constants.SharedPreferences.KEY_SHOW_SYSTEM_PROGRESS, checked).apply();
            }
        });
    }

    /**
     * 初始化UI-是否开启任务
     */
    private void initTaskUI(){
        sivTask= (SettingItemView) findViewById(R.id.siv_task);
        sivTask.check(ServiceStatusUtils.isActive(this, KillProgressService.class));
        sivTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked=!sivTask.isChecked();
                sivTask.check(checked);
                if(checked){
                    startService(new Intent(ProgressSettingActivity.this, KillProgressService.class));
                }else{
                    stopService(new Intent(ProgressSettingActivity.this, KillProgressService.class));
                }
            }
        });
    }

    /**
     * 初始化UI-设置清理任务时间间隔
     */
    private void initTaskIntervalUI(){
        scvTaskInterval= (SettingClickView) findViewById(R.id.scv_task_interval);
        int which=mPref.getInt(Constants.SharedPreferences.KEY_TASK_INTERVAL,0);
        scvTaskInterval.setDesc(Constants.TaskInterval.DESC[which]);
        AlertDialog.Builder builder=new AlertDialog.Builder(ProgressSettingActivity.this);
        builder.setTitle("时间间隔设置");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(Constants.TaskInterval.DESC, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //保存
                String desc = Constants.TaskInterval.DESC[which];
                mPref.edit().putInt(Constants.SharedPreferences.KEY_TASK_INTERVAL,which).apply();
                scvTaskInterval.setDesc(desc);
                dialog.dismiss();
                //重新启动service
                if(sivTask.isChecked()){
                    stopService(new Intent(ProgressSettingActivity.this, KillProgressService.class));
                    startService(new Intent(ProgressSettingActivity.this, KillProgressService.class));
                }
            }
        });
        final AlertDialog dialog=builder.create();
        scvTaskInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

}
