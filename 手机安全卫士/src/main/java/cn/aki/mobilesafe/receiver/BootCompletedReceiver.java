package cn.aki.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/27.
 * 手机启动事件监听
 */
public class BootCompletedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mPref=context.getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,Context.MODE_PRIVATE);
        boolean protect=mPref.getBoolean(Constants.SharedPreferences.KEY_PROTECT,false);
        //开启保护
        if(protect){
            String sim=mPref.getString(Constants.SharedPreferences.KEY_SIM,null);
            //绑定sim卡
            if(!TextUtils.isEmpty(sim)){
                TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simSerialNumber=telephonyManager.getSimSerialNumber();
                //sim卡变更
                if(!sim.equals(simSerialNumber)){
                    String safePhone=mPref.getString(Constants.SharedPreferences.KEY_SAFE_PHONE, "");
                    //绑定安全手机
                    if(!TextUtils.isEmpty(safePhone)){
                        SmsManager smsManager=SmsManager.getDefault();
                        smsManager.sendTextMessage(safePhone,null,"sim card changed",null,null);
                    }
                }
            }
        }
    }
}
