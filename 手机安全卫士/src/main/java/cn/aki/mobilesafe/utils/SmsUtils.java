package cn.aki.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

//import static android.provider.Telephony.Sms.*;

/**
 * Created by Administrator on 2016/1/6.
 * 短信工具类
 */
public class SmsUtils {
    //api19之后取android.provider.Telephony.Sms中的同名常量
    private static final String CONTENT_URI="content://sms";
    private static final String ADDRESS="address";
    private static final String BODY="body";
    private static final String TYPE="type";
    private static final String DATE="date";

    /**
     * 进度视图接口
     */
    public interface BackUpProgress{
        /**
         * 统计进度总和
         * @param count 总计
         */
        void before(int count);

        /**
         * 更新进度
         * @param progress 完成进度
         */
        void progress(int progress);

        /**
         * 完成
         */
        void success();

        /**
         * 失败
         */
        void fail();
    }

    /**
     * 备份短信
     */
    public static void backUpSms(Context context,BackUpProgress backUpProgress) {
        //SD卡存在
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor=contentResolver.query(Uri.parse(CONTENT_URI), new String[]{ADDRESS, BODY, TYPE, DATE}, null, null, null);
        if(cursor!=null){
            //设置进度总数
            backUpProgress.before(cursor.getCount());
            //进度
            int progress=0;
            //xml封装
            XmlSerializer serializer= Xml.newSerializer();
            OutputStream os=null;
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            try {
                os=new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "backUp.xml"));
                //设置保存路径
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "smss");
                //总条数
                serializer.attribute(null, "size", String.valueOf(cursor.getCount()));
                while(cursor.moveToNext()){
                    serializer.startTag(null,"sms");
                    String address=cursor.getString(cursor.getColumnIndex(ADDRESS));
                    String body=cursor.getString(cursor.getColumnIndex(BODY));
                    int type=cursor.getInt(cursor.getColumnIndex(TYPE));
                    long date=cursor.getLong(cursor.getColumnIndex(DATE));
                    serializer.startTag(null, ADDRESS);
                    serializer.text(address);
                    serializer.endTag(null, ADDRESS);
                    serializer.startTag(null, BODY);
                    //内容加密
                    serializer.text(EncryptUtils.encrypt(body));
//                    serializer.text(body);
                    serializer.endTag(null, BODY);
                    serializer.startTag(null, TYPE);
                    serializer.text(Integer.toString(type));
                    serializer.endTag(null, TYPE);
                    serializer.startTag(null,DATE);
                    serializer.text(sdf.format(date));
                    serializer.endTag(null,DATE);
                    serializer.endTag(null,"sms");
                    //设置进度
                    progress++;
                    backUpProgress.progress(progress);
                }
                serializer.endTag(null,"smss");
                serializer.endDocument();
                backUpProgress.success();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                 if(os!=null){
                     try {
                         os.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
            }
            cursor.close();
        }
        //失败
        backUpProgress.fail();
    }
}
