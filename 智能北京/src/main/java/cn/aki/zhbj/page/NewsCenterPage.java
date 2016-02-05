package cn.aki.zhbj.page;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/4.
 * 新闻中心页
 */
public class NewsCenterPage extends BasePage{
    private Categories mCategories;//数据
    public NewsCenterPage(Context context, String title, SlidingMenu menu) {
        super(context, title, menu, true);
    }

    @Override
    protected void initContentView(FrameLayout frameLayout) {
        super.initContentView(frameLayout);
    }

    @Override
    public void initData() {
        super.initData();
        RequestParams params=new RequestParams(C.Url.CATEGORIES);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new Gson();
                mCategories=gson.fromJson(result, Categories.class);
                if(mOnDataChangeListener!=null){
                    mOnDataChangeListener.onDataChange(mCategories);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext,"加载数据失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
}
