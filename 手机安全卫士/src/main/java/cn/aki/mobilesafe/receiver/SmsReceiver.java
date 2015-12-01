package cn.aki.mobilesafe.receiver;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.service.LocationService;

/**
 * Created by Administrator on 2015/11/30.
 * 拦截短信指令
 */
public class SmsReceiver extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus= (Object[]) intent.getExtras().get("pdus");
        SharedPreferences mPref=context.getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,Context.MODE_PRIVATE);
        boolean protect=mPref.getBoolean(Constants.SharedPreferences.KEY_PROTECT,false);
        String safePhone=mPref.getString(Constants.SharedPreferences.KEY_SAFE_PHONE,null);
        if(pdus!=null&&protect&&!TextUtils.isEmpty(safePhone)){
            //设备管理器
            DevicePolicyManager mDPM=(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName mDeviceAdminSample=new ComponentName(context, DeviceAdminSampleReceiver.class);
            for(Object pdu:pdus){
                SmsMessage message=SmsMessage.createFromPdu((byte[])pdu);
                String body=message.getMessageBody();
                String from=message.getOriginatingAddress();
                //由安全手机发送
                if(safePhone.equals(from)){
                    switch (body){
                        //GPS定位
                        case Constants.SmsCommand.LOCATION:
                            context.startService(new Intent(context, LocationService.class));
                            SmsManager smsManager=SmsManager.getDefault();
                            String location=mPref.getString(Constants.SharedPreferences.KEY_LOCATION, null);
                            if(!TextUtils.isEmpty(location)){
                                smsManager.sendTextMessage(safePhone,null,location,null,null);
                            }
                            abortBroadcast();
                            break;
                        //播放报警音乐
                        case Constants.SmsCommand.ALARM:
                            MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.ylzs);
                            mediaPlayer.setVolume(1F,1F);
                            mediaPlayer.start();
                            abortBroadcast();
                            break;
                        //删除数据
                        case Constants.SmsCommand.WIPE_DATA:
                            if(mDPM.isAdminActive(mDeviceAdminSample)){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                    mDPM.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
                                }else{
                                    mDPM.wipeData(0);
                                }
                            }
                            abortBroadcast();
                            break;
                        //锁屏
                        case Constants.SmsCommand.LOCK_SCREEN:
                            if(mDPM.isAdminActive(mDeviceAdminSample)){
                                mDPM.lockNow();
                                //设置锁屏密码
//                                mDPM.resetPassword(safePhone,0);
                            }
                            abortBroadcast();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
