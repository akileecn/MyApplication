package cn.aki.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导4
 */
public class SafeStep4Activity extends BaseSafeStepActivity {
    private CheckBox cbProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step4);
        cbProtect= (CheckBox) findViewById(R.id.cb_protect);
        boolean protect=mPref.getBoolean(Constants.SharedPreferences.KEY_PROTECT,false);
        cbProtect.setChecked(protect);
        if(protect){
            cbProtect.setText("你已开启防盗保护");
        }else{
            cbProtect.setText("你未开启防盗保护");
        }
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.edit().putBoolean(Constants.SharedPreferences.KEY_PROTECT,isChecked).apply();
                if(isChecked){
                    cbProtect.setText("你已开启防盗保护");
                }else{
                    cbProtect.setText("你未开启防盗保护");
                }
            }
        });
    }

    @Override
    void toPrevious() {
        startActivity(new Intent(this, SafeStep3Activity.class));
        overridePendingTransition(R.anim.tran_left_in, R.anim.tran_right_out);
        finish();

    }

    @Override
    void toNext() {
        startActivity(new Intent(this, SafeActivity.class));
        overridePendingTransition(R.anim.tran_right_in, R.anim.tran_left_out);
        mPref.edit().putBoolean(Constants.SharedPreferences.KEY_SAFE_GUIDED,true).apply();
        finish();
    }

}
