package cn.aki.zhbj.page;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.page.detail.BaseDetailPage;
import cn.aki.zhbj.page.detail.NewsDetailPage;
import cn.aki.zhbj.page.detail.PhotoDetailPage;
import cn.aki.zhbj.page.detail.SimpleDetailPage;
import cn.aki.zhbj.utils.CacheUtils;

/**
 * Created by Administrator on 2016/2/4.
 * 新闻中心页
 */
public class NewsCenterPage extends BasePage{
    private Categories mCategories;//数据
    private List<BaseDetailPage> mBaseDetailPageList;//详情页
    private Button btnShowType;//组图显示按钮
    public NewsCenterPage(Context context, String title, SlidingMenu menu) {
        super(context, title, menu, true);

    }

    @Override
    protected void initView() {
        super.initView();
        btnShowType= (Button) mRootView.findViewById(R.id.btn_show_type);
    }

    @Override
    public void initData() {
        super.initData();
        //解析缓存
        String cache=CacheUtils.getCache(mContext,C.Url.CATEGORIES);
        if(cache!=null){
            parseData(cache);
        }
        RequestParams params=new RequestParams(C.Url.CATEGORIES);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //保存缓存
                CacheUtils.saveCache(mContext,C.Url.CATEGORIES,result);
                parseData(result);
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

    /**
     * 分析数据
     */
    private void parseData(String result){
        Gson gson = new Gson();
        mCategories = gson.fromJson(result, Categories.class);
        //触发事件
        triggerOnDataChangeEvent(mCategories);
        mBaseDetailPageList=new ArrayList<>();
        for(Categories.ParentMenu menu:mCategories.data){
            switch (menu.type){
                //新闻
                case 1:
                    mBaseDetailPageList.add(new NewsDetailPage(mContext,menu,mMenu));
                    break;
                //组图
                case 2:
                    mBaseDetailPageList.add(new PhotoDetailPage(mContext,menu,btnShowType));
                    break;
                default:
                    mBaseDetailPageList.add(new SimpleDetailPage(mContext,menu));
                    break;
            }
        }
        updateContent(0);
    }

    @Override
    public void updateContent(int position) {

        //标题
        tvTitle.setText(mCategories.data.get(position).title);
        //内容
        flContent.removeAllViews();
        //填充的详情页
        BaseDetailPage page=mBaseDetailPageList.get(position);
        //仅组图时显示切换菜单
        if(page instanceof PhotoDetailPage){
            btnShowType.setVisibility(View.VISIBLE);
        }else{
            btnShowType.setVisibility(View.INVISIBLE);
        }
        flContent.addView(page.getView());
    }

}
