package cn.aki.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.view.SettingItemView;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导2
 */
public class SafeStep2Activity extends BaseSafeStepActivity {
    private SettingItemView sivSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step2);
        sivSim= (SettingItemView) findViewById(R.id.siv_sim);
        String sim=mPref.getString(Constants.SharedPreferences.KEY_SIM,null);
        if(TextUtils.isEmpty(sim)){
            sivSim.check(false);
        }else{
            sivSim.check(true);
        }
        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivSim.isChecked()){
                    sivSim.check(false);
                    mPref.edit().remove(Constants.SharedPreferences.KEY_SIM).apply();
                }else{
                    sivSim.check(true);
                    //保存sim卡信息
                    TelephonyManager telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber=telephonyManager.getSimSerialNumber();
                    mPref.edit().putString(Constants.SharedPreferences.KEY_SIM,simSerialNumber).apply();
                }
            }
        });

    }

    @Override
    public void toNext(){
        if(sivSim.isChecked()){
            startActivity(new Intent(this, SafeStep3Activity.class));
            overridePendingTransition(R.anim.tran_right_in, R.anim.tran_left_out);
            finish();
        }else{
            Toast.makeText(this,"请绑定sim卡",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toPrevious(){
        startActivity(new Intent(this, SafeStep1Activity.class));
        overridePendingTransition(R.anim.tran_left_in, R.anim.tran_right_out);
        finish();
    }

}
