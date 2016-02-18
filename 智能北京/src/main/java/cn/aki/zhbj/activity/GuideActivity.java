package cn.aki.zhbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.common.C;

/**
 * Created by Administrator on 2016/2/1.
 * 引导页
 */
public class GuideActivity extends Activity {
    private static final int[] imageResIds={R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};//引导页图片

    private SharedPreferences mPref;
    private ViewPager vpGuide;//滑动图片视图
    private List<View> mImageList;//滑动图片集合
    private Button btnStart;//开始体验按钮
    private LinearLayout llPoints;//灰点容器
    private ImageView ivRedPoint;//红点
    private int mPointDistance;//灰点间距(含宽度)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref= C.Sp.getSharedPreferences(this);
        initView();
    }

    private void initView(){
        //通过代码隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        /**初始化图片*/
        vpGuide= (ViewPager) findViewById(R.id.vp_guide);
        mImageList=new ArrayList<>();
        for(int redId :imageResIds){
            View view=new ImageView(this);
            view.setBackgroundResource(redId);
            mImageList.add(view);
        }
        GuidePagerAdapter adapter=new GuidePagerAdapter();
        vpGuide.setAdapter(adapter);
        /**初始化圆点*/
        ivRedPoint= (ImageView) findViewById(R.id.iv_red_point);
        llPoints= (LinearLayout) findViewById(R.id.ll_points);
        for(int i=0;i<imageResIds.length;i++){
            View pointView=new View(this);
            pointView.setBackgroundResource(R.drawable.shape_point_grey);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(10,10);
            if(i>0){
                params.leftMargin=20;
            }
            llPoints.addView(pointView,params);
        }
        /**跳转按钮*/
        btnStart= (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref.edit().putBoolean(C.Sp.KEY_IS_GUIDED,true).apply();
                startActivity(new Intent(GuideActivity.this,MainActivity.class));
                finish();
            }
        });
        /**红点随图片滚动,按钮显示*/
        //监听视图树，获得圆点间距
        llPoints.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPointDistance=llPoints.getChildAt(1).getLeft()-llPoints.getChildAt(0).getLeft();
                //注销监听
                llPoints.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //监听图片滑动
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //设置红点偏移
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin= (int) (mPointDistance*(position+positionOffset));
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //仅末页显示按钮
                if(position==imageResIds.length-1){
                    btnStart.setVisibility(View.VISIBLE);
                }else{
                    btnStart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 视图切换适配器
     */
    private class GuidePagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=mImageList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}
