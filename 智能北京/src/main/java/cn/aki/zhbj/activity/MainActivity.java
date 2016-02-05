package cn.aki.zhbj.activity;

import android.app.Activity;
import android.os.Bundle;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.aki.zhbj.R;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.fragment.MainContentFragment;
import cn.aki.zhbj.fragment.MainLeftMenuFragment;
import cn.aki.zhbj.page.BasePage;

/**
 * Created by Administrator on 2016/2/2.
 * 首页
 */
public class MainActivity extends Activity{
    private MainContentFragment mContentFragment;//内容
    private MainLeftMenuFragment mLeftMenuFragment;//菜单


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        setContentView(R.layout.activity_main);
        /**加载侧滑页面*/
        // configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(this);
        //显示方式
        menu.setMode(SlidingMenu.LEFT);
        //触发显示的范围(仅边缘处可以滑动)
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffset(300);
//        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu_main);
        /**加载fragment*/
        mContentFragment=new MainContentFragment();
        mContentFragment.setSlidingMenu(menu);
        mLeftMenuFragment=new MainLeftMenuFragment();
        mLeftMenuFragment.setSlidingMenu(menu);
        /**数据交互*/
        mContentFragment.setOnDataChangeListener(new BasePage.OnDataChangeListener() {
            @Override
            public void onDataChange(BasePage page,Categories data) {
                mLeftMenuFragment.updateList(page,data);
            }
        });
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content,mContentFragment)
                .replace(R.id.fl_left_menu,mLeftMenuFragment)
                .commit();
    }

}
