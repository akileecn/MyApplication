package cn.aki.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import cn.aki.mobilesafe.bean.BlackNumber;
import cn.aki.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单拦截服务
 */
public class BlackListService extends Service {
    private BroadcastReceiver mSmsReceiver;
    private BlackNumberDao blackNumberDao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberDao=new BlackNumberDao(this);
        initSmsBlackList();
    }

    /**
     * 初始化短信拦截
     */
    private void initSmsBlackList(){
        mSmsReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object[] pdus= (Object[]) intent.getExtras().get("pdus");
                if(pdus!=null){
                    for(Object pdu:pdus){
                        SmsMessage msg=SmsMessage.createFromPdu((byte[]) pdu);
                        String number=msg.getOriginatingAddress();
                        BlackNumber blackNumber=blackNumberDao.get(number);
                        //短信拦截
                        if(blackNumber!=null&&(blackNumber.getMode()==BlackNumber.MODE_ALL||blackNumber.getMode()==BlackNumber.MODE_SMS)){
                            abortBroadcast();
                        }
                    }
                }
            }
        };
        IntentFilter filter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mSmsReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSmsReceiver);
    }
}
