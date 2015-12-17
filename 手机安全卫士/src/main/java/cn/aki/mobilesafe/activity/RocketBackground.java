package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/12/17.
 * 火箭背景
 */
public class RocketBackground extends Activity{
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final long DURATION=1000L;
        setContentView(R.layout.activity_rocket_background);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
        alphaAnimation.setDuration(DURATION);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.iv_bottom).startAnimation(alphaAnimation);
        findViewById(R.id.iv_top).startAnimation(alphaAnimation);
        //动画结束后自动关闭
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },DURATION);
    }
}
