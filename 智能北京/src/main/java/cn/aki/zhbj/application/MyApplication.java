package cn.aki.zhbj.application;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Administrator on 2016/2/4.
 * 自定义应用类
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}

