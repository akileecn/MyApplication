package cn.aki.zhbj.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.page.BasePage;
import cn.aki.zhbj.page.NewsCenterPage;
import cn.aki.zhbj.page.SimplePage;

/**
 * Created by Administrator on 2016/2/3.
 * 主页面内容部件
 */
public class MainContentFragment extends Fragment {
    private ViewPager vpContent;//内容
    private RadioGroup rgTabs;//tab按钮
    private List<BasePage> mPageList;
    private PagerAdapter mContentAdapter;
    private SlidingMenu mMenu;//活动菜单
    private BasePage.OnDataChangeListener mOnDataChangeListener;//数据加载监听器

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main_content, container, false);
        vpContent= (ViewPager) rootView.findViewById(R.id.vp_content);
        rgTabs= (RadioGroup) rootView.findViewById(R.id.rg_tabs);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPageList=new ArrayList<>();
        mPageList.add(new SimplePage(getActivity(),"首页",mMenu,false,"页面详情-首页"));
        NewsCenterPage newsCenterPage=new NewsCenterPage(getActivity(), "新闻中心",mMenu);
        if(mOnDataChangeListener!=null){
            newsCenterPage.setOnDataChangeListener(mOnDataChangeListener);
        }
        mPageList.add(newsCenterPage);
        mPageList.add(new SimplePage(getActivity(), "智慧服务",mMenu,true, "智慧服务-新闻中心"));
        mPageList.add(new SimplePage(getActivity(), "政务",mMenu,true, "页面详情-政务"));
        mPageList.add(new SimplePage(getActivity(), "设置",mMenu,false, "页面详情-设置"));
        mContentAdapter=new MyContentAdapter();
        vpContent.setAdapter(mContentAdapter);
        //tab点击切换
        rgTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = -1;
                switch (checkedId) {
                    case R.id.rb_home:
                        position = 0;
                        break;
                    case R.id.rb_news:
                        position = 1;
                        break;
                    case R.id.rb_service:
                        position = 2;
                        break;
                    case R.id.rb_affair:
                        position = 3;
                        break;
                    case R.id.rb_setting:
                        position = 4;
                        break;
                }
                if(position!=-1){
                    vpContent.setCurrentItem(position, false);
                    mPageList.get(position).initData();
                }
            }
        });
        rgTabs.check(R.id.rb_home);
    }

    /**
     * ViewPager适配器
     */
    private class MyContentAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mPageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=mPageList.get(position).createView();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 设置控制的菜单
     */
    public void setSlidingMenu(SlidingMenu menu){
        mMenu=menu;
    }

    /**
     * 数据交互
     */
    public void setOnDataChangeListener(BasePage.OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }
}
