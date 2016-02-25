package cn.aki.zhbj.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/2/25.
 * http请求
 */
public abstract class HttpTask<T> {
    protected RequestParams mParams;

    public HttpTask(String url) {
        mParams=new RequestParams(url);
    }

    /**
     * http请求
     */
    public T execute(){
        HttpURLConnection connection=null;
        InputStream is=null;
        try {
            connection= (HttpURLConnection) new URL(mParams.url).openConnection();
            connection.setConnectTimeout(mParams.connectTimeOut);
            connection.setReadTimeout(mParams.readTimeOut);
            connection.setRequestMethod(mParams.requestMethod);
            connection.connect();
            is=connection.getInputStream();
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                return doHttp(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        return null;
    }

    protected abstract T doHttp(HttpURLConnection connection) throws IOException;

    /**
     * 请求参数
     */
    public static class RequestParams{
        public String url;
        /**请求方式GET/POST*/
        public String requestMethod=REQUEST_METHOD_GET;
        public int connectTimeOut=TIME_OUT_DEFAULT;
        public int readTimeOut=TIME_OUT_DEFAULT;
        public static final String REQUEST_METHOD_GET="GET";
//        public static final String REQUEST_METHOD_POST="POST";
        private static final int TIME_OUT_DEFAULT=5000;
        public RequestParams(String url){
            this.url=url;
        }
    }
}
