package cn.aki.zhbj.page;

import android.content.Context;
import android.view.View;

import cn.aki.zhbj.utils.cache.ICache;
import cn.aki.zhbj.utils.cache.JsonFileCache;

/**
 * Created by Administrator on 2016/2/4.
 * 页面基类
 */
public abstract class Page {
    protected Context mContext;
    protected View mRootView;//根布局
    protected ICache<String> mJsonCache;//json缓存

    public Page(Context context) {
        mContext = context;
        mJsonCache=new JsonFileCache(context);
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 创建视图
     */
    public View getView(){
        if(mRootView==null){
            initView();
        }
        return mRootView;
    }

    /**
     * 加载数据
     */
    public void initData(){}

}
