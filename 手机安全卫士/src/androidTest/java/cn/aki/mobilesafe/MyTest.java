package cn.aki.mobilesafe;

import android.test.AndroidTestCase;

import cn.aki.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by Administrator on 2016/1/12.
 * 测试
 */
public class MyTest extends AndroidTestCase{
    public void test(){
        SystemInfoUtils.getMemoryInfo(getContext());
    }
}
