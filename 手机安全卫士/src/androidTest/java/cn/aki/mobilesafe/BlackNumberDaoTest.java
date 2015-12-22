package cn.aki.mobilesafe;

import android.test.AndroidTestCase;

import java.util.Random;

import cn.aki.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单测试类
 */
public class BlackNumberDaoTest  extends AndroidTestCase {

    public void testInit(){
        long startNumber=13000000000L;
        Random r=new Random();
        BlackNumberDao dao=new BlackNumberDao(getContext());
        for(int i=0;i<100;i++){
            dao.add(String.valueOf(startNumber),r.nextInt(3)+1);
            startNumber++;
        }
        System.out.println("done");
    }
}
