package cn.aki.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.activity.RocketBackground;

/**
 * Created by Administrator on 2015/12/17.
 * 火箭服务
 */
public class RocketService extends Service {
    private View mRocketView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private AnimationDrawable mRocketAnimation;
    private int tempX;
    private int tempY;
    private int winWidth;
    private int winHeight;
    private static final int WHAT_LAUNCH_ROCKET=1;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==WHAT_LAUNCH_ROCKET){
                int step=msg.arg1;
                params.y-=step;
                mWindowManager.updateViewLayout(mRocketView,params);
            }
            return true;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mRocketView = View.inflate(this, R.layout.view_rocket,null);
        ImageView ivRocket= (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        mRocketAnimation= (AnimationDrawable) ivRocket.getBackground();
        //获得屏幕尺寸
        Point winSize=new Point();
        mWindowManager.getDefaultDisplay().getSize(winSize);
        winWidth=winSize.x;
        winHeight=winSize.y;
        initRocketView();
    }

    /**
     * 初始化火箭
     */
    private void initRocketView(){
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
        params.x=0;
        params.y=0;
        //监听拖动
        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        tempX= (int) event.getRawX();
                        tempY= (int) event.getRawY();
                        mRocketAnimation.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX= (int) event.getRawX();
                        int newY= (int) event.getRawY();
                        int dx=newX-tempX;
                        int dy=newY-tempY;
                        int left=params.x+dx;
                        int top=params.y+dy;
                        int right=left+mRocketView.getWidth();
                        int bottom=top+mRocketView.getHeight();
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
                            mWindowManager.updateViewLayout(mRocketView,params);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //发射火箭
                        if(params.y>winHeight-mRocketView.getHeight()-100){
                            launchRocket();
                        }
                        break;
                    default:
                        break;
                }
                //设置为false时无法获得后续动作
                return true;
            }
        });
        //放到window上
        mWindowManager.addView(mRocketView,params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除火箭
        if(mWindowManager!=null&&mRocketView!=null){
            mWindowManager.removeView(mRocketView);
        }
    }

    /**
     * 发射火箭
     */
    private void launchRocket(){
        //启动背景
        Intent intent=new Intent(this, RocketBackground.class);
        //从service打开activity必须添加该标记
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        params.x=winWidth/2-mRocketView.getWidth()/2;
        params.y=winHeight-mRocketView.getHeight();
        mWindowManager.updateViewLayout(mRocketView, params);
        final int step=params.y/10;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    Message message=handler.obtainMessage();
                    message.what=WHAT_LAUNCH_ROCKET;
                    message.arg1=step;
                    handler.sendMessageDelayed(message,100*(i+1));
                }
            }
        }).start();
    }

}
