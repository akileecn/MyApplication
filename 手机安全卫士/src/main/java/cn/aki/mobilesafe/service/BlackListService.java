package cn.aki.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.aki.mobilesafe.bean.BlackNumber;
import cn.aki.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单拦截服务
 */
public class BlackListService extends Service {
    private BroadcastReceiver mSmsReceiver;
    private BlackNumberDao blackNumberDao;
    private PhoneStateListener mPhoneListener;
    private TelephonyManager mTelephonyManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberDao = new BlackNumberDao(this);
        initSmsIntercept();
        initPhoneIntercept();
    }

    /**
     * 初始化短信拦截
     */
    private void initSmsIntercept() {
        mSmsReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mSmsReceiver, filter);
    }

    private void initPhoneIntercept() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneListener = new MyPhoneStateListener();
        //监听电话状态
        mTelephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 短信广播接收
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                    String number = msg.getOriginatingAddress();
                    BlackNumber blackNumber = blackNumberDao.get(number);
                    //短信拦截
                    if (blackNumber != null && ((blackNumber.getMode() & BlackNumber.MODE_SMS) != 0)) {
                        abortBroadcast();
                    }
                }
            }
        }
    }

    /**
     * 电话状态监听
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                //来电时
                case TelephonyManager.CALL_STATE_RINGING:
                    BlackNumber blackNumber = blackNumberDao.get(incomingNumber);
                    if (blackNumber != null && (blackNumber.getMode() & BlackNumber.MODE_PHONE) != 0) {
                        System.err.println("黑名单来电:" + incomingNumber);
                        //注册通话记录观察者
                        //直接调用deleteCallLog无法删除最新的一条记录!
                        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,true,new MyContentObserver(null,incomingNumber));
                        endCall();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 监听通话日志
     */
    private class MyContentObserver extends ContentObserver{
        private String number;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String number) {
            super(handler);
            this.number=number;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(number);
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            //返回所有定义的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除来电
     * @param number 来电
     */
    private void deleteCallLog(String number) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.NUMBER + "=?", new String[]{number});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSmsReceiver);
        mTelephonyManager.listen(mPhoneListener,PhoneStateListener.LISTEN_NONE);
    }
}
