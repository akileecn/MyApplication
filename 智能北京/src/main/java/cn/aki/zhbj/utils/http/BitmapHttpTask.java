package cn.aki.zhbj.utils.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2016/2/25.
 * 图片下载
 */
public class BitmapHttpTask extends HttpTask<Bitmap> {
    public BitmapHttpTask(String url) {
        super(url);
    }

    @Override
    protected Bitmap doHttp(HttpURLConnection connection) throws IOException {
        return BitmapFactory.decodeStream(connection.getInputStream());
    }
}
