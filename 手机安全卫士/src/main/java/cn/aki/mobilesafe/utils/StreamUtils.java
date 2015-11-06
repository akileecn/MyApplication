package cn.aki.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/10/10.
 */
public class StreamUtils {
    public static String toString(InputStream is)throws IOException{
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int len;
        while((len=is.read(buff))!=-1) {
            os.write(buff, 0, len);
        }
        String result=os.toString();
        is.close();
        os.close();
        return result;
    }
}
