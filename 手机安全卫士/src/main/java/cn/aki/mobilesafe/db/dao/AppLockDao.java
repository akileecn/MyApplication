package cn.aki.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static cn.aki.mobilesafe.db.dao.AppLockOpenHelper.*;

/**
 * Created by Administrator on 2016/1/26.
 * 应用锁DAO
 */
public class AppLockDao {
    private AppLockOpenHelper mHelper;

    public AppLockDao(Context context) {
        mHelper=new AppLockOpenHelper(context);
    }

    /**
     * 添加到被锁应用
     * @param packageName 应用包名
     */
    public void add(String packageName){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_PACKAGE_NAME,packageName);
        db.insert(TABLE,COLUMN_PACKAGE_NAME,values);
        db.close();
    }

    /**
     * 移除被锁应用
     * @param packageName 应用包名
     */
    public int delete(String packageName){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        int count=db.delete(TABLE, COLUMN_PACKAGE_NAME + "=?", new String[]{packageName});
        db.close();
        return count;
    }


    /**
     * 判断应用是否被锁
     * @param packageName 应用包名
     */
    public boolean exists(String packageName){
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor=db.query(TABLE, new String[]{COLUMN_PACKAGE_NAME}, COLUMN_PACKAGE_NAME+"=?", new String[]{packageName}, null, null, null);
        boolean result=cursor.moveToNext();
        cursor.close();
        return result;
    }
}
