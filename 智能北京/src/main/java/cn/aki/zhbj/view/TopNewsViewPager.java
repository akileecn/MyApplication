package cn.aki.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/2/17.
 * 热点新闻viewPager
 */
public class TopNewsViewPager extends ViewPager{
    private float startX;
    private float startY;
    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 控制touch事件的传递
     * 效果与不重写相同
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=ev.getRawX();
                startY=ev.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX=ev.getRawX();
                float currentY=ev.getRawY();
                //左右划
                if(Math.abs(startX-currentX)>Math.abs(startY-currentY)){
                    //首页右划,末页左划
                    if(currentX>startX&&getCurrentItem()==0
                            ||currentX<startX&&getCurrentItem()==getAdapter().getCount()-1){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    //上下划
                }else{
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
