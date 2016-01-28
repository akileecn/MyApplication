package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.service.AppLockService;
import cn.aki.mobilesafe.utils.UiUtils;

/**
 * Created by Administrator on 2016/1/27.
 * 程序锁输入密码页面
 */
public class AppLockPasswordActivity extends Activity{
    private static final String PASSWORD="123";
    private EditText etPassword;
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI(){
        setContentView(R.layout.activity_app_lock_password);
        etPassword= (EditText) findViewById(R.id.et_password);
        btnSubmit= (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=etPassword.getText().toString();
                if(PASSWORD.equals(password)){
                    Intent intent=new Intent(Constants.Action.APP_LOCK_EXCLUDE);
                    //获得启动时传过来的包名
                    String packageName=getIntent().getStringExtra(AppLockService.EXTRA_PACKAGE_NAME);
                    intent.putExtra(AppLockService.EXTRA_PACKAGE_NAME,packageName);
                    sendBroadcast(intent);
                    finish();
                }else{
                    UiUtils.showToast(AppLockPasswordActivity.this,"密码错误");
                }
            }
        });
    }

    /**
     * 自定义键盘按下
     */
    public void customKeyDown(View view){
        String oldPassword=etPassword.getText().toString();
        switch (view.getId()){
            //数字
            case R.id.btn_no_0:
            case R.id.btn_no_1:
            case R.id.btn_no_2:
            case R.id.btn_no_3:
            case R.id.btn_no_4:
            case R.id.btn_no_5:
            case R.id.btn_no_6:
            case R.id.btn_no_7:
            case R.id.btn_no_8:
            case R.id.btn_no_9:
                String addWord=((Button)view).getText().toString();
                etPassword.setText(oldPassword+addWord);
                break;
            //清除
            case R.id.btn_clear:
                etPassword.setText("");
                break;
            //删除
            case R.id.btn_delete:
                if(!TextUtils.isEmpty(oldPassword)){
                    etPassword.setText(oldPassword.substring(0,oldPassword.length()-1));
                }
                break;
            default:
                break;

        }
    }

}
