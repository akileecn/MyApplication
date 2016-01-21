package cn.aki.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.aki.mobilesafe.bean.VirusInfo;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2016/1/21.
 * 查毒DAO
 */
public class AntivirusDao {

    /**
     * 根据MD5查询病毒信息
     * @return  没有则返回null
     */
    public static VirusInfo getByMd5(String md5){
        SQLiteDatabase db=SQLiteDatabase.openDatabase(Constants.DataBase.AntiVirus,null,SQLiteDatabase.OPEN_READONLY);
        String sql="select md5" +
                    "    ,name" +
                    "    ,desc" +
                    " from datable" +
                    " where md5=?";
        Cursor cursor=db.rawQuery(sql,new String[]{md5});
        VirusInfo vi=null;
        if(cursor!=null){
            if(cursor.moveToNext()){
                String name=cursor.getString(1);
                String desc=cursor.getString(2);
                vi=new VirusInfo();
                vi.setDesc(desc);
                vi.setName(name);
            }
            cursor.close();
        }
        db.close();
        return vi;
    }
}
