package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.utils.StreamUtils;

public class SplashActivity extends Activity {

    private TextView tvVersion;
    private TextView tvDownload;
    //当前版本信息
    private int mVersionCode;
    private String mVersionName;
    //最新版本信息
    private String mNewVersionName;
    private int mNewVersionCode;
    private String mNewDescription;
    private String mNewDownloadUrl;
    private SharedPreferences mSharedPreferences;

    //加载最新版本信息的返回Code
    private static final int WHAT_NEED_UPDATE=0;
    private static final int WHAT_URL_ERROR=1;
    private static final int WHAT_NET_ERROR=2;
    private static final int WHAT_JSON_ERROR=3;
    private static final int WHAT_GO_HOME=4;

    private Handler handler=new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        x.Ext.init(getApplication());
        x.Ext.setDebug(true);
        setContentView(R.layout.activity_splash);
        tvVersion= (TextView) findViewById(R.id.tv_version);
        tvDownload= (TextView) findViewById(R.id.tv_download);
        getCurrentVersion();
        tvVersion.setText("版本号:" + mVersionName);
        mSharedPreferences=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        //是否自动更新
        boolean autoUpdate=mSharedPreferences.getBoolean(Constants.SharedPreferences.KEY_AUTO_UPDATE,true);
        if(autoUpdate){
            checkUpdate();
        }else{
            //延迟发送信息
            handler.sendEmptyMessageDelayed(WHAT_GO_HOME,2000);
        }
        RelativeLayout rlRoot= (RelativeLayout) findViewById(R.id.rl_root);
        //动画显示
        AlphaAnimation animation=new AlphaAnimation(0.3f,1f);
        animation.setDuration(2000);
        rlRoot.startAnimation(animation);
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
            long startTime=System.currentTimeMillis();
            try {
                HttpURLConnection conn= (HttpURLConnection) new URL(Constants.UPDATE_URL).openConnection();
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
                    }else{
                        message.what=WHAT_GO_HOME;
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
                long usedTime=System.currentTimeMillis()-startTime;
                //至少2s等待
                if(usedTime<2000){
                    try {
                        Thread.sleep(2000-usedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendMessage(message);
            }
            }
        }).start();

    }

    private void createUpdateDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        //builder.setCancelable(false); //禁止取消对话框，用户体验差，不推荐使用
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                toHome();
            }
        });
        builder.setTitle("最新版本:"+mNewVersionName);
        builder.setMessage(mNewDescription);
        builder.setPositiveButton("确认更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadNewVersion();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toHome();
            }
        });
        builder.show();
    }

    private static class MyHandler extends Handler{
        private WeakReference<SplashActivity> weakReference;

        public MyHandler(SplashActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity=weakReference.get();
            switch (msg.what){
                case WHAT_NEED_UPDATE:
                    activity.createUpdateDialog();
                    break;
                case WHAT_URL_ERROR:
                    Toast.makeText(activity, "请求异常", Toast.LENGTH_SHORT).show();
                    activity.toHome();
                    break;
                case WHAT_NET_ERROR:
                    Toast.makeText(activity,"网络异常",Toast.LENGTH_SHORT).show();
                    activity.toHome();
                    break;
                case WHAT_JSON_ERROR:
                    Toast.makeText(activity,"数据异常",Toast.LENGTH_SHORT).show();
                    activity.toHome();
                    break;
                case WHAT_GO_HOME:
                    activity.toHome();
                default:
                    break;
            }
        }
    }

    /**
     * 跳转到首页
     */
    private void toHome(){
        Intent intent =new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 下载新版本
     */
    private void downLoadNewVersion(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            tvDownload.setVisibility(View.VISIBLE);
            x.http().get(new RequestParams(mNewDownloadUrl), new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(SplashActivity.this, "下载失败！", Toast.LENGTH_LONG).show();
                    toHome();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    tvDownload.setText("下载:" + current * 100 / total + "%");
                }
            });
        }else{
            Toast.makeText(this, "没有找到sdcard！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toHome();
    }
}
