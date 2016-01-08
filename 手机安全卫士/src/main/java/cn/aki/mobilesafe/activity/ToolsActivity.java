package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.utils.SmsUtils;
import cn.aki.mobilesafe.utils.UiUtils;

/**
 * Created by Administrator on 2015/12/2.
 * 高级工具
 */
public class ToolsActivity extends Activity {
    private ProgressBar pbBackUp;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        pbBackUp= (ProgressBar) findViewById(R.id.pb_back_up);
    }

    public void toAddress(View view){
        startActivity(new Intent(this, AddressActivity.class));
        finish();
    }

    /**
     * 备份短信
     */
    public void backUpSms(View view){
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("备份短信");
        mProgressDialog.setMessage("拼命备份中...");
        mProgressDialog.show();
        new Thread(){
            @Override
            public void run() {
                SmsUtils.backUpSms(ToolsActivity.this,new SmsUtils.BackUpProgress(){

                    @Override
                    public void before(int count) {
                        mProgressDialog.setMax(count);
                        pbBackUp.setMax(count);
                    }

                    @Override
                    public void progress(int progress) {
                        mProgressDialog.setProgress(progress);
                        pbBackUp.setProgress(progress);
                    }

                    @Override
                    public void success() {
                        mProgressDialog.dismiss();
                        UiUtils.showToast(ToolsActivity.this, "备份成功");
                    }

                    @Override
                    public void fail() {
                        mProgressDialog.dismiss();
                        UiUtils.showToast(ToolsActivity.this,"备份失败");
                    }
                });
            }
        }.start();
    }

}
