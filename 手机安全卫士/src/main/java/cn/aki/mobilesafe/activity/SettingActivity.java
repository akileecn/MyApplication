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
import cn.aki.mobilesafe.service.AddressService;
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
    private SettingItemView sivShowAddress; //电话归属地
    private SharedPreferences mPref;//参数
    private SettingClickView scvAddressStyle;   //归属地样式
    private SettingClickView scvAddressLocation;    //归属地位置
    private SettingItemView sivRocket;  //火箭

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        sivShowAddress= (SettingItemView) findViewById(R.id.siv_show_address);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        scvAddressStyle= (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressLocation= (SettingClickView) findViewById(R.id.scv_address_location);
        sivRocket= (SettingItemView) findViewById(R.id.siv_rocket);
        initUpdate();
        initShowAddress();
        initAddressStyle();
        initAddressLocation();
        initRocket();
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
     * 初始化电话归属地显示
     */
    private void initShowAddress(){
        //根据服务是否启动判断状态
        sivShowAddress.check(ServiceStatusUtils.isActive(this, AddressService.class));
        sivShowAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sivShowAddress.isChecked()){
                    sivShowAddress.check(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }else{
                    sivShowAddress.check(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    /**
     * 初始化归属地样式
     */
    private void initAddressStyle(){
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE,0);
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
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE,0);
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
     * 初始化火箭
     */
    private void initRocket(){
        sivRocket.check(ServiceStatusUtils.isActive(this,RocketService.class));
        sivRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivRocket.isChecked()){
                    sivRocket.check(false);
                    stopService(new Intent(SettingActivity.this, RocketService.class));
                }else{
                    sivRocket.check(true);
                    startService(new Intent(SettingActivity.this,RocketService.class));
                }
            }
        });
    }
}
