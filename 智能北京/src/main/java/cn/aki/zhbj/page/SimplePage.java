package cn.aki.zhbj.page;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/4.
 * 简单页面
 */
public class SimplePage extends BasePage {
    /**
     * 构造方法
     * @param context 上下文
     */
    public SimplePage(Context context,String title,SlidingMenu menu,boolean isShowMenu) {
        super(context, title, menu,isShowMenu);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView view=new TextView(mContext);
        view.setText("简单页面-"+mTitle);
        view.setTextColor(Color.RED);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        view.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flContent.addView(view, params);
    }

    @Override
    public void initData() {
        super.initData();
        triggerOnDataChangeEvent(Categories.EMPTY_DATA);
    }
}
