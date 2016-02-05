package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/5.
 * 新闻详情页
 */
public class NewsDetailPage extends BaseDetailPage{
    private ViewPager vpTab;//tab切换页
    private List<Categories.Node> mNodeList;
    public NewsDetailPage(Context context, Categories.ParentNode data) {
        super(context, data);
        mNodeList=data.getChildren();
    }

    @Override
    protected void initView() {
        mRootView=View.inflate(mContext, R.layout.detail_page_news,null);
        vpTab= (ViewPager) mRootView.findViewById(R.id.vp_tab);
        vpTab.setAdapter(new MyNewsPagerAdapter());
    }

    private class MyNewsPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mNodeList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=new SimpleDetailPage(mContext,mNodeList.get(position)).getView();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
