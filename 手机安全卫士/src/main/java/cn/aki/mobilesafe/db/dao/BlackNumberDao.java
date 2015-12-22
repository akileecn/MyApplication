package cn.aki.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.bean.BlackNumber;
import static cn.aki.mobilesafe.db.dao.BlackNumberOpenHelper.*;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单DAO
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper helper;
    public BlackNumberDao(Context context){
        helper=new BlackNumberOpenHelper(context);
    }

    /**
     * 添加
     * @param number 手机号
     * @param mode 模式 1电话拦截 2短信拦截 3全部拦截
     * @return 是否成功
     */
    public boolean add(String number,Integer mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_NUMBER,number);
        values.put(COLUMN_MODE,mode);
        long rowId=db.insert(TABLE,null,values);
        db.close();
        return rowId!=-1;
    }

    /**
     * 查询所有
     * @return 完整黑名单
     */
    public List<BlackNumber> queryAll(){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query(TABLE, new String[]{COLUMN_NUMBER, COLUMN_MODE}
                , null, null, null, null, null);
        List<BlackNumber> list=getListFromCursor(cursor);
        cursor.close();
        db.close();
        SystemClock.sleep(3000);
        return list;
    }

    /**
     * 删除黑名单
     * @param number 手机号码
     * @return 是否删除成功
     */
    public boolean delete(String number){
        SQLiteDatabase db=helper.getWritableDatabase();
        int rowSize=db.delete(TABLE, COLUMN_NUMBER + "=?", new String[]{number});
        db.close();
        return rowSize>0;
    }

    /**
     * 分页查询
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @return 黑名单集合
     */
    public List<BlackNumber> queryPage(int pageIndex,int pageSize){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query(TABLE, new String[]{COLUMN_NUMBER, COLUMN_MODE}, null, null, null, null, null, (pageIndex - 1) * pageSize + "," + pageSize);
        List<BlackNumber> list=getListFromCursor(cursor);
        cursor.close();
        db.close();
        SystemClock.sleep(1000);
        return list;
    }

    //从cursor封装数据
    private List<BlackNumber> getListFromCursor(Cursor cursor){
        List<BlackNumber> list=new ArrayList<>();
        while(cursor.moveToNext()){
            BlackNumber blackNumber=new BlackNumber();
            String number=cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
            Integer mode=cursor.getInt(cursor.getColumnIndex(COLUMN_MODE));
            blackNumber.setNumber(number);
            blackNumber.setMode(mode);
            list.add(blackNumber);
        }
        return list;
    }

    /**
     * 获得一个黑名单
     * @param number 手机号码
     * @return 黑名单，没有则返回null
     */
    public BlackNumber get(String number){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query(TABLE, new String[]{COLUMN_MODE}, COLUMN_NUMBER + "=?", new String[]{number}, null, null, null);
        BlackNumber blackNumber=null;
        if(cursor.moveToNext()){
            blackNumber=new BlackNumber();
            Integer mode=cursor.getInt(cursor.getColumnIndex(COLUMN_MODE));
            blackNumber.setNumber(number);
            blackNumber.setMode(mode);
        }
        cursor.close();
        db.close();
        return blackNumber;
    }
}
