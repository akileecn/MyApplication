package cn.aki.zhbj.page;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.aki.zhbj.R;
import cn.aki.zhbj.data.response.Categories;

/**
 * Created by Administrator on 2016/2/4.
 * 基本页面
 */
public abstract class BasePage extends Page{
    protected String mTitle;
    private boolean mIsShowMenu;
    protected TextView tvTitle;//标题
    private Button btnMenu;//菜单按钮
    protected FrameLayout flContent;//内容
    protected SlidingMenu mMenu;//菜单
    protected OnDataChangeListener mOnDataChangeListener;//数据加载监听器

    public BasePage(Context context, String title,SlidingMenu menu,boolean isShowMenu) {
        super(context);
        mTitle = title;
        mMenu = menu;
        mIsShowMenu = isShowMenu;
    }

    @Override
    protected void initView(){
        mRootView=View.inflate(mContext, R.layout.item_main_content,null);
        tvTitle= (TextView) mRootView.findViewById(R.id.tv_title);
        btnMenu= (Button) mRootView.findViewById(R.id.btn_menu);
        flContent= (FrameLayout) mRootView.findViewById(R.id.fl_content);
        tvTitle.setText(mTitle);
        btnMenu.setVisibility(mIsShowMenu ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void initData(){
        if(mMenu!=null){
            mMenu.setSlidingEnabled(mIsShowMenu);
            if(mMenu.isMenuShowing()){
                mMenu.toggle();
            }
            if(mIsShowMenu){
                //菜单点击事件
                btnMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMenu.toggle();
                    }
                });
            }
        }
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    /**
     * 触发数据变化事件
     * @param data 新数据
     */
    protected void triggerOnDataChangeEvent(Categories data){
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChange(this, data);
        }
    }

    /**
     * 数据交互接口
     */
    public interface OnDataChangeListener {
        void onDataChange(BasePage page,Categories data);
    }

    /**
     * 根据数据修改显示内容
     * @param position 页面序号
     */
    public void updateContent(int position){}

    /**
     * 是否显示菜单
     */
    public boolean isShowMenu(){
        return mIsShowMenu;
    }
}
