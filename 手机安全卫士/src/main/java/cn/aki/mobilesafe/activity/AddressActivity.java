package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.db.dao.AddressDao;

/**
 * Created by Administrator on 2015/12/2.
 * 归属地
 */
public class AddressActivity extends Activity{
    private EditText etPhone;
    private TextView tvResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etPhone= (EditText) findViewById(R.id.et_phone);
        tvResult= (TextView) findViewById(R.id.tv_result);
        //动态查询
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address=AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void queryAddress(View view){
        String phone=etPhone.getText().toString();
        if(TextUtils.isEmpty(phone)){
            //输出框抖动
            Animation anim= AnimationUtils.loadAnimation(this,R.anim.shake);
            etPhone.startAnimation(anim);
            //手机震动
            Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{200,300,200,300},-1);   //-1不重复
        }else{
            String address=AddressDao.getAddress(phone);
            tvResult.setText(address);
        }
    }
}
