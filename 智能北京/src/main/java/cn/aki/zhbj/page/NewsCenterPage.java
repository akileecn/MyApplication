package cn.aki.zhbj.page;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.page.detail.BaseDetailPage;
import cn.aki.zhbj.page.detail.NewsDetailPage;
import cn.aki.zhbj.page.detail.SimpleDetailPage;

/**
 * Created by Administrator on 2016/2/4.
 * 新闻中心页
 */
public class NewsCenterPage extends BasePage{
    private Categories mCategories;//数据
    private List<BaseDetailPage> mBaseDetailPageList;//详情页
    public NewsCenterPage(Context context, String title, SlidingMenu menu) {
        super(context, title, menu, true);

    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public void initData() {
        super.initData();
        RequestParams params=new RequestParams(C.Url.CATEGORIES);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                mCategories = gson.fromJson(result, Categories.class);
                //触发事件
                triggerOnDataChangeEvent(mCategories);
                mBaseDetailPageList=new ArrayList<>();
                boolean first=true;
                for(Categories.ParentNode node:mCategories.getData()){
                    if(first){
                        mBaseDetailPageList.add(new NewsDetailPage(mContext,node));
                        first=false;
                    }else{
                        mBaseDetailPageList.add(new SimpleDetailPage(mContext,node));
                    }
                }
                updateContent(0);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void updateContent(int position) {
        //标题
        tvTitle.setText(mCategories.getData().get(position).getTitle());
        //内容
        flContent.removeAllViews();
        flContent.addView(mBaseDetailPageList.get(position).getView());
    }

}
