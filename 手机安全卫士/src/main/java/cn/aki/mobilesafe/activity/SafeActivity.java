package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.receiver.DeviceAdminSampleReceiver;

/**
 * Created by Administrator on 2015/11/19.
 * 手机防盗
 */
public class SafeActivity extends Activity {
    private static final int REQUEST_CODE_ADMIN_ACTIVE=1;

    private SharedPreferences mPref;
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        boolean safeGuided=mPref.getBoolean(Constants.SharedPreferences.KEY_SAFE_GUIDED,false);
        if(safeGuided){
            setContentView(R.layout.activity_safe);
            //显示安全手机号码
            String phone=mPref.getString(Constants.SharedPreferences.KEY_SAFE_PHONE, "");
            TextView tvPhone= (TextView) findViewById(R.id.tv_phone);
            tvPhone.setText(phone);
            //显示保护开启状态
            boolean protect=mPref.getBoolean(Constants.SharedPreferences.KEY_PROTECT,false);
            ImageView ivProtect= (ImageView) findViewById(R.id.iv_protect);
            if(protect){
                ivProtect.setImageResource(R.drawable.lock);
            }else{
                ivProtect.setImageResource(R.drawable.unlock);
            }
            //设备管理器
            mDPM= (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDeviceAdminSample=new ComponentName(this, DeviceAdminSampleReceiver.class);
        }else{
            toSafeGuide(null);
        }
    }
    /**
     * 跳转到向导页
     */
    public void toSafeGuide(View view){
        startActivity(new Intent(this, SafeStep1Activity.class));
        finish();
    }

    /**
     * 激活设备管理器
     */
    public void enableDeviceAdmin(View view){
        if(mDPM.isAdminActive(mDeviceAdminSample)){
            Toast.makeText(this,"已激活",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mDeviceAdminSample);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "自定义的设备管理器");
            startActivityForResult(intent, REQUEST_CODE_ADMIN_ACTIVE);
        }
    }

}
