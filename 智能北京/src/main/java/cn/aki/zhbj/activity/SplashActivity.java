package cn.aki.zhbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import cn.aki.zhbj.R;
import cn.aki.zhbj.common.C;

/**
 * Created by Administrator on 2016/2/1.
 * 闪屏页
 */
public class SplashActivity extends Activity{
    private ImageView ivSplash;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref= C.Sp.getSharedPreferences(this);
        initView();
    }

    private void initView(){
        setContentView(R.layout.activity_splash);
        ivSplash= (ImageView) findViewById(R.id.iv_splash);
        //3合1动画
        AnimationSet animationSet=new AnimationSet(false);
        RotateAnimation rotateAnimation=new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1.0F,0,1.0F,Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1.0F);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(1000);
        animationSet.setFillAfter(true);
        //监听动画结束事件
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isGuided=mPref.getBoolean(C.Sp.KEY_IS_GUIDED,false);
                //完成引导的直接跳转到首页
                if(isGuided){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivSplash.startAnimation(animationSet);

    }
}
