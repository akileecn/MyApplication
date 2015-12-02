package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/27.
 * 手机防盗向导基类
 */
public abstract class BaseSafeStepActivity extends Activity {
    protected SharedPreferences mPref;
    //手势监听
    private GestureDetector mDetector;
    /**
     * x轴距离
     */
    private static final float X_DISTANCE=100;
    /**
     * y轴距离
     */
    private static final float Y_DISTANCE=200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,MODE_PRIVATE);
        mDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(e1.getRawY()-e2.getRawY())<Y_DISTANCE){
                    //左滑
                    if(e1.getRawX()-e2.getRawX()>X_DISTANCE){
                        toNext();
                        return true;
                    }
                    //右滑
                    if(e2.getRawX()-e1.getRawX()>X_DISTANCE){
                        toPrevious();
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 跳转到下一页
     */
    abstract void toPrevious();

    /**
     * 跳转到上一页
     */
    abstract void toNext();

    public void next(View view){
        toNext();
    }

    public void previous(View view){
        toPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
