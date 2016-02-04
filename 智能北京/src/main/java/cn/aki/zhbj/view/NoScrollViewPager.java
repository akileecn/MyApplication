package cn.aki.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/2/4.
 * 禁止滑动ViewPager
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 处理事件
     * @return 是否已消耗事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //不处理
        return false;
    }

    /**
     * 是否拦截事件(父->子,调用onInterceptTouchEvent;再从子->父,调用onTouchEvent)
     * @return 是否拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //不拦截
        return false;
    }
}
