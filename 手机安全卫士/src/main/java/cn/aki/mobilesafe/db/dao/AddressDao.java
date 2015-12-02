package cn.aki.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/12/2.
 * 归属地查询
 */
public class AddressDao {
    /**
     * 查询归属地
     * @param number 号码
     * @return 归属地
     */
    public static String getAddress(String number){
        SQLiteDatabase db=SQLiteDatabase.openDatabase(Constants.DataBase.Address,null,SQLiteDatabase.OPEN_READONLY);
        //手机
        String mobileSql="select d2.location" +
                " from data2 d2" +
                " inner join data1 d1" +
                "   on d1.outkey=d2.id" +
                " where d1.id=?";
        String telephoneSql="select location from data2 where area=? or area=?";
        //电话号码
        String address="未知号码";
        if(!TextUtils.isEmpty(number)&&number.matches("^\\d+$")){
            Cursor cursor=null;
            if(number.matches("^1[3-8]\\d{9}$")){
                cursor=db.rawQuery(mobileSql,new String[]{number.substring(0,7)});
                if(cursor.moveToNext()){
                    address=cursor.getString(0);
                }
            }else if(number.startsWith("0")&&number.matches("^\\d{11,12}$")) {
                cursor=db.rawQuery(telephoneSql,new String[]{number.substring(1,3),number.substring(1,4)});
                if(cursor.moveToNext()){
                    address=cursor.getString(0);
                }
            }else{
                switch (number.length()){
                    case 3:
                        address="报警电话";
                        break;
                    case 5:
                        address="模拟器";
                        break;
                    case 8:
                        address="本地电话";
                        break;
                    default:
                        break;
                }
            }
            if(cursor!=null){
                cursor.close();
            }
        }
        db.close();
        return address;
    }
}
