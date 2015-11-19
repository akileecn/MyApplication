package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/19.
 * 手机防盗
 */
public class SafeActivity extends Activity {
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        boolean guided=mPref.getBoolean(Constants.SharedPreferences.KEY_GUIDED,false);
        setContentView(R.layout.activity_safe);
        //TODO

    }

}
