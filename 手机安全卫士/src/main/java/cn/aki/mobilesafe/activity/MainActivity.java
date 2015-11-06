package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.utils.StreamUtils;

public class MainActivity extends Activity {

    private TextView tvVersion;
    //当前版本信息
    private int mVersionCode;
    private String mVersionName;
    //最新版本信息
    private String mNewVersionName;
    private int mNewVersionCode;
    private String mNewDescription;
    private String mNewDownloadUrl;

    //加载最新版本信息的返回Code
    private static final int WHAT_NEED_UPDATE=0;
    private static final int WHAT_URL_ERROR=1;
    private static final int WHAT_NET_ERROR=2;
    private static final int WHAT_JSON_ERROR=3;

    private Handler handler=new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvVersion= (TextView) findViewById(R.id.tv_version);
        getCurrentVersion();
        tvVersion.setText("版本号:" + mVersionName);
        checkUpdate();
    }

    /**
     * 获得当前版本信息
     */
    private void getCurrentVersion(){
        try {
            PackageInfo packageInfo=getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionCode=packageInfo.versionCode;
            mVersionName=packageInfo.versionName;
            Log.d("test","mVersionCode:"+mVersionCode+",mVersionName:"+mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查更新
     */
    private void checkUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                try {
                    HttpURLConnection conn= (HttpURLConnection) new URL("http://192.168.2.37:8080/update.json").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String jsonStr= StreamUtils.toString(is);
                        //{"versionName": "2.0", "versionCode": 2, "description": "新增NB功能,赶紧体验!!!", "downloadUrl":  "http://www.baidu.com"}
                        JSONObject json=new JSONObject(jsonStr);
                        mNewDescription=json.getString("description");
                        mNewDownloadUrl=json.getString("downloadUrl");
                        mNewVersionCode=json.getInt("versionCode");
                        mNewVersionName=json.getString("versionName");
                        if(mNewVersionCode>mVersionCode){
                            message.what=WHAT_NEED_UPDATE;
                        }
                    }
                } catch (MalformedURLException e) {
                    //链接错误
                    message.what=WHAT_URL_ERROR;
                } catch (IOException e) {
                    //网络错误
                    message.what=WHAT_NET_ERROR;
                } catch (JSONException e) {
                    //json错误
                    message.what=WHAT_JSON_ERROR;
                }finally {
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    private void createUpdateDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("最新版本:"+mNewVersionName);
        builder.setMessage(mNewDescription);
        builder.setPositiveButton("确认更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
            }
        });
        builder.show();
    }

    private static class MyHandler extends Handler{
        private WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity=weakReference.get();
            Log.d("test", "msg:" + msg.what);
            switch (msg.what){
                case WHAT_NEED_UPDATE:
                    activity.createUpdateDialog();
                    break;
                case WHAT_URL_ERROR:
                    Toast.makeText(activity, "请求异常", Toast.LENGTH_SHORT).show();
                    break;
                case WHAT_NET_ERROR:
                    Toast.makeText(activity,"网络异常",Toast.LENGTH_SHORT).show();
                    break;
                case WHAT_JSON_ERROR:
                    Toast.makeText(activity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    //TODO
                    break;
            }
        }
    }

}
