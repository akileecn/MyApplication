package cn.aki.mobilesafe;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import cn.aki.mobilesafe.utils.EncryptUtils;

/**
 * Created by Administrator on 2016/1/8.
 * 加密测试
 */
public class EncryptTest extends AndroidTestCase {
    public void test(){
        String encrypt=EncryptUtils.encrypt("12345");
        String decrypt=EncryptUtils.decrypt(encrypt);
        System.err.println("encrypt:"+encrypt);
        System.err.println("decrypt:"+decrypt);
        Assert.assertEquals("12345",decrypt);
    }
}
