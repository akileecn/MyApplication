package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导3
 */
public class SafeStep3Activity extends BaseSafeStepActivity {
    private static final int REQUEST_CODE_DEFAULT=0;
    private EditText et_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step3);
        et_phone= (EditText) findViewById(R.id.et_phone);
        String phone=mPref.getString(Constants.SharedPreferences.KEY_SAFE_PHONE,"");
        et_phone.setText(phone);
    }

    @Override
    void toPrevious() {
        startActivity(new Intent(this, SafeStep2Activity.class));
        overridePendingTransition(R.anim.tran_left_in, R.anim.tran_right_out);
        finish();
    }

    @Override
    void toNext() {
        String phone=et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"请填写安全号码",Toast.LENGTH_SHORT).show();
        }else{
            //保存手机号码
            mPref.edit().putString(Constants.SharedPreferences.KEY_SAFE_PHONE, phone).apply();
            startActivity(new Intent(this, SafeStep4Activity.class));
            overridePendingTransition(R.anim.tran_right_in, R.anim.tran_left_out);
            finish();
        }
    }

    public void toContacts(View view){
        Intent intent=new Intent(this,ContactsActivity.class);
        startActivityForResult(intent,REQUEST_CODE_DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_DEFAULT&&resultCode==Activity.RESULT_OK){
            String phone=data.getStringExtra(ContactsActivity.EXTRA_KEY);
            et_phone.setText(phone);
        }
    }
}
