package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/5.
 * 简单详情页
 */
public class SimpleDetailPage extends BaseDetailPage{

    public SimpleDetailPage(Context context, Categories.Menu data) {
        super(context, data);
    }

    @Override
    protected void initView() {
        TextView view=new TextView(mContext);
        view.setText("简单详情页-"+mData.title);
        view.setTextColor(Color.RED);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        view.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        mRootView=view;
    }

}
