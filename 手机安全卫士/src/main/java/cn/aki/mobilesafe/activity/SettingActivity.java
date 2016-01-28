package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.service.AddressService;
import cn.aki.mobilesafe.service.AppLockService;
import cn.aki.mobilesafe.service.BlackListService;
import cn.aki.mobilesafe.service.RocketService;
import cn.aki.mobilesafe.utils.ServiceStatusUtils;
import cn.aki.mobilesafe.view.SettingClickView;
import cn.aki.mobilesafe.view.SettingItemView;

/**
 * Created by Administrator on 2015/11/13.
 * 设置中心
 */
public class SettingActivity extends Activity {
    private SettingItemView sivUpdate;//自动更新
//    private SettingItemView sivShowAddress; //电话归属地`
    private SharedPreferences mPref;//参数
    private SettingClickView scvAddressStyle;   //归属地样式
    private SettingClickView scvAddressLocation;    //归属地位置
//    private SettingItemView sivRocket;  //火箭
//    private SettingItemView sivBlackList;  //黑名单
//    private SettingItemView sivAppLock; //程序锁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        scvAddressStyle= (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressLocation= (SettingClickView) findViewById(R.id.scv_address_location);
        initUpdate();
        initAddressStyle();
        initAddressLocation();
        //电话归属地显示
        initSiv(R.id.siv_show_address,AddressService.class);
        //小火箭
        initSiv(R.id.siv_rocket,RocketService.class);
        //黑名单
        initSiv(R.id.siv_black_list, BlackListService.class);
        //程序锁
        initSiv(R.id.siv_app_lock, AppLockService.class);
    }

    /**
     * 初始化自动更新
     */
    private void initUpdate(){
        boolean autoUpdate=mPref.getBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE,true);
        sivUpdate.check(autoUpdate);
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()) {
                    sivUpdate.check(false);
                    mPref.edit().putBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE, false).apply();
                } else {
                    sivUpdate.check(true);
                    mPref.edit().putBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE, true).apply();
                }
            }
        });
    }

    /**
     * 初始化归属地样式
     */
    private void initAddressStyle(){
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE, 0);
        scvAddressStyle.setDesc(Constants.AddressStyle.DESC[style]);
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressStyleDialog();
            }
        });
    }

    /**
     * 显示归属地样式对话框
     */
    private void showAddressStyleDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE, 0);
        builder.setSingleChoiceItems(Constants.AddressStyle.DESC, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE, which).apply();
                scvAddressStyle.setDesc(Constants.AddressStyle.DESC[which]);
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    /**
     * 初始化归属地位置
     */
    private void initAddressLocation(){
        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开拖拽页面
                startActivity(new Intent(SettingActivity.this, DragAddressActivity.class));
            }
        });
    }

    /**
     * 初始化SettingItemView的公共方法
     * @param sivId SettingItemView的ID
     * @param serviceClass 对应服务class
     */
    private void initSiv(int sivId, final Class<? extends Service> serviceClass){
        final SettingItemView siv= (SettingItemView) findViewById(sivId);
        //根据服务是否启动判断状态
        siv.check(ServiceStatusUtils.isActive(this,serviceClass));
        siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv.isChecked()){
                    siv.check(false);
                    stopService(new Intent(SettingActivity.this,serviceClass));
                }else{
                    siv.check(true);
                    startService(new Intent(SettingActivity.this,serviceClass));
                }
            }
        });
    }
}
