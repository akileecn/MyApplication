package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/5.
 * 新闻详情页
 */
public class NewsDetailPage extends BaseDetailPage implements View.OnClickListener{
    private SlidingMenu mMenu;
    private ViewPager vpTab;//tab切换页
    private List<Categories.Menu> mMenuList;
    private ImageButton ibNext;
    public NewsDetailPage(Context context, Categories.ParentMenu data,SlidingMenu menu) {
        super(context, data);
        mMenuList=data.children;
        mMenu=menu;
    }

    @Override
    protected void initView() {
        mRootView=View.inflate(mContext, R.layout.detail_page_news,null);
        vpTab= (ViewPager) mRootView.findViewById(R.id.vp_tab);
        vpTab.setAdapter(new MyNewsPagerAdapter());
        //viewPager指示器
        TabPageIndicator indicator= (TabPageIndicator) mRootView.findViewById(R.id.tpi_indicator);
        indicator.setViewPager(vpTab);
        ibNext= (ImageButton) mRootView.findViewById(R.id.ib_next);
        ibNext.setOnClickListener(this);
        MyOnPageChangeListener onPageChangeListener=new MyOnPageChangeListener();
        vpTab.addOnPageChangeListener(onPageChangeListener);
        indicator.setOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_next:
                vpTab.setCurrentItem(vpTab.getCurrentItem()+1);
                break;
            default:
                break;
        }
    }

    private class MyNewsPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=new NewsTabDetailPage(mContext,mMenuList.get(position)).getView();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * viewPager指示器取标题会用到
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mMenuList.get(position).title;
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //仅首页才能显示菜单
            if(position==0){
                mMenu.setSlidingEnabled(true);
            }else{
                mMenu.setSlidingEnabled(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
