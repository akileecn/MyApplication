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
 * Created by Administrator on 2015/11/19.
 * 手机防盗
 */
public class SafeActivity extends Activity {
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        boolean safeGuided=mPref.getBoolean(Constants.SharedPreferences.KEY_SAFE_GUIDED,false);
        if(safeGuided){
            setContentView(R.layout.activity_safe);
        }else{
            toSafeGuide(null);
        }

    }
    /**
     * 跳转到向导页
     */
    public void toSafeGuide(View view){
        startActivity(new Intent(this,SafeStep1Activity.class));
        finish();
    }

}
