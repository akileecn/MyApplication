package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导3
 */
public class SafeStep3Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step3);
    }

    public void next(View view){
        startActivity(new Intent(this, SafeStep4Activity.class));
        overridePendingTransition(R.anim.tran_right_in, R.anim.tran_left_out);
        finish();
    }

    public void previous(View view){
        startActivity(new Intent(this, SafeStep2Activity.class));
        overridePendingTransition(R.anim.tran_left_in, R.anim.tran_right_out);
        finish();
    }
}
