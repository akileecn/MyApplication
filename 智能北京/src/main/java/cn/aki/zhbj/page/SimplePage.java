package cn.aki.zhbj.page;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by Administrator on 2016/2/4.
 * 简单页面
 */
public class SimplePage extends BasePage {
    private String mContent;
    /**
     * 构造方法
     * @param context 上下文
     * @param content 内容
     */
    public SimplePage(Context context,String title,SlidingMenu menu,boolean isShowMenu,String content) {
        super(context, title, menu,isShowMenu);
        mContent=content;
    }

    @Override
    protected void initContentView(FrameLayout frameLayout) {
        TextView view=new TextView(mContext);
        view.setText(mContent);
        view.setTextColor(Color.RED);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        view.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(view,params);
    }
}
