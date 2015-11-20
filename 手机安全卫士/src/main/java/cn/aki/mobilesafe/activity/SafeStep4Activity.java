package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导4
 */
public class SafeStep4Activity extends Activity {
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step4);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
    }

    public void next(View view){
        startActivity(new Intent(this, SafeActivity.class));
        overridePendingTransition(R.anim.tran_right_in, R.anim.tran_left_out);
        mPref.edit().putBoolean(Constants.SharedPreferences.KEY_SAFE_GUIDED,true).apply();
        finish();
    }

    public void previous(View view){
        startActivity(new Intent(this, SafeStep3Activity.class));
        overridePendingTransition(R.anim.tran_left_in, R.anim.tran_right_out);
        finish();
    }
}
