package cn.aki.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单数据库辅助类
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE="black_number";
    public static final String COLUMN_NUMBER="number";
    public static final String COLUMN_MODE="mode";
    public BlackNumberOpenHelper(Context context){
        super(context,"blackList.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table black_number (_id integer primary key autoincrement,number varchar(20),mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
