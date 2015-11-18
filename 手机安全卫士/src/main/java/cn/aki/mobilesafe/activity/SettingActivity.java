package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.view.SettingItemView;

/**
 * Created by Administrator on 2015/11/13.
 * 设置中心
 */
public class SettingActivity extends Activity {
    private SettingItemView sivUpdate;//自动更新
    private SharedPreferences mSharedPreferences;//参数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        mSharedPreferences=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        //初始化自动更新
        boolean autoUpdate=mSharedPreferences.getBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE,true);
        sivUpdate.check(autoUpdate);
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivUpdate.isChecked()){
                    sivUpdate.check(false);
                    mSharedPreferences.edit().putBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE,false).apply();
                }else{
                    sivUpdate.check(true);
                    mSharedPreferences.edit().putBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE, true).apply();
                }
            }
        });
    }
}
