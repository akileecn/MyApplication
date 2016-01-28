package cn.aki.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/26.
 * 应用锁数据库帮助类
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE="app_lock";
    public static final String COLUMN_PACKAGE_NAME="package_name";

    public AppLockOpenHelper(Context context) {
        super(context, "appLock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE+" (_id integer primary key autoincrement,"+COLUMN_PACKAGE_NAME+" varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
