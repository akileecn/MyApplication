package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/12/2.
 * 高级工具
 */
public class ToolsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
    }

    public void toAddress(View view){
        startActivity(new Intent(this,AddressActivity.class));
        finish();
    }
}
