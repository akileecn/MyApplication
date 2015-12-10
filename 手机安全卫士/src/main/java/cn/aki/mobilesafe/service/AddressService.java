package cn.aki.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.db.dao.AddressDao;

/**
 * Created by Administrator on 2015/12/4.
 * 来电去电归属地显示
 */
public class AddressService extends Service{
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private MyOutgoingReceiver myOutgoingReceiver;
    private WindowManager mWindowManager;
    private View mToastView;
    private SharedPreferences mPref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,Context.MODE_PRIVATE);
        mTelephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener=new MyPhoneStateListener();
        //监听来电
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //接收去电广播
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        myOutgoingReceiver=new MyOutgoingReceiver();
        registerReceiver(myOutgoingReceiver, intentFilter);
        //toast
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 来电监听器
     */
    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            //电话铃响
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String address=AddressDao.getAddress(incomingNumber);
                    showToast(address);
                    break;
                //电话闲置时
                case TelephonyManager.CALL_STATE_IDLE:
                    dismissToast();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 监听去电
     */
    class MyOutgoingReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String address=AddressDao.getAddress(getResultData());
            Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 注销监听
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(myOutgoingReceiver);
    }

    /**
     * 显示toast
     * @param message 信息
     */
    private void showToast(String message){
        if(mToastView==null){
            mToastView = View.inflate(AddressService.this, R.layout.toast_address,null);
        }
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE,0);
        int x=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,0);
        int y=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,0);
        //TODO 在屏幕定位
        mToastView.setBackgroundResource(Constants.AddressStyle.DRAWABLE[style]);
        TextView tvMessage = (TextView) mToastView.findViewById(R.id.tv_message);

        tvMessage.setText(message);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //从Toast.class里抄的
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.x=x;
        params.y=y;
        //放到window上
        mWindowManager.addView(mToastView,params);
    }

    /**
     * 取消toast
     */
    private void dismissToast(){
        if(mWindowManager!=null&&mToastView!=null){
            mWindowManager.removeView(mToastView);
        }
    }
}
