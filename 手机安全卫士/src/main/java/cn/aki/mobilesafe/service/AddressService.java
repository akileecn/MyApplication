package cn.aki.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
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

    //归属地toast位置
    private WindowManager.LayoutParams params;
    private int tempX;
    private int tempY;

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
            showToast(address);
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
        //设置样式
        int style=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_STYLE,0);
        mToastView.setBackgroundResource(Constants.AddressStyle.DRAWABLE[style]);
        TextView tvMessage = (TextView) mToastView.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        params = new WindowManager.LayoutParams();
        //设置视图在window中的属性
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //电话层权限
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //将原坐标中中间修改为左上
        params.gravity= Gravity.START|Gravity.TOP;
        params.x=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,0);
        params.y=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,0);
        //获得屏幕尺寸
        Point winSize=new Point();
        mWindowManager.getDefaultDisplay().getSize(winSize);
        final int winWidth=winSize.x;
        final int winHeight=winSize.y;
        //监听拖动
        mToastView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        tempX= (int) event.getRawX();
                        tempY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX= (int) event.getRawX();
                        int newY= (int) event.getRawY();
                        int dx=newX-tempX;
                        int dy=newY-tempY;
                        int left=params.x+dx;
                        int top=params.y+dy;
                        int right=left+mToastView.getWidth();
                        int bottom=top+mToastView.getHeight();
                        boolean changed=false;
                        if(left>0&&right<winWidth){
                            params.x=left;
                            tempX=newX;
                            changed=true;
                        }
                        if(top>0&&bottom<winHeight){
                            params.y=top;
                            tempY=newY;
                            changed=true;
                        }
                        if(changed){
                            mWindowManager.updateViewLayout(mToastView,params);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //保存
                        SharedPreferences.Editor editor=mPref.edit();
                        editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,params.x);
                        editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,params.y);
                        editor.apply();
                        break;
                    default:
                        break;
                }
                //设置为false时无法获得后续动作
                return true;
            }
        });
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
