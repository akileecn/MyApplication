package cn.aki.zhbj.page.detail;

import android.content.Context;

import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.page.Page;

/**
 * Created by Administrator on 2016/2/5.
 * 基本详情页
 */
public abstract class BaseDetailPage extends Page {
    protected Categories.Node mData;//数据源

    public BaseDetailPage(Context context,Categories.Node data) {
        super(context);
        mData=data;
    }

}
