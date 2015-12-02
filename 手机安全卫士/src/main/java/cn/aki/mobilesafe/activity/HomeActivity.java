package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.utils.Md5Utils;

/**
 * Created by Administrator on 2015/11/10.
 * 首页面
 */
public class HomeActivity extends Activity{
    private GridView glList;
    private SharedPreferences mSharedPreferences;
    private String[] items={"手机防盗","通讯卫士","软件管理"
                            ,"进程管理","流量统计","手机杀毒"
                            ,"缓存清理","高级工具","设置中心"};
    private int[] images={R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps
                        ,R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan
                        ,R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSharedPreferences=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        glList= (GridView) findViewById(R.id.gl_list);
        glList.setAdapter(new MyAdapter());
        glList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    showPasswordDialog();
                    break;
                case 7:
                    startActivity(new Intent(HomeActivity.this,ToolsActivity.class));
                    break;
                //设置中心
                case 8:
                    startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                    break;
                default:
                    break;
            }
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(HomeActivity.this,R.layout.home_item,null);
            ImageView imageView= (ImageView) view.findViewById(R.id.item_img);
            TextView textView= (TextView) view.findViewById(R.id.item_name);
            imageView.setImageResource(images[position]);
            textView.setText(items[position]);
            return view;
        }
    }

    /**
     * 显示密码对话框
     */
    private void showPasswordDialog(){
        final String oldPassword=mSharedPreferences.getString(Constants.SharedPreferences.KEY_PASSWORD,null);
        //创建对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //密码不存在
        if(TextUtils.isEmpty(oldPassword)){
            View dialogView=View.inflate(this,R.layout.dialog_set_password,null);
            builder.setView(dialogView);
            final EditText etPassword= (EditText) dialogView.findViewById(R.id.et_password);
            final EditText etConfirmPassword= (EditText) dialogView.findViewById(R.id.et_confirm_password);
            Button btnOK= (Button) dialogView.findViewById(R.id.btn_ok);
            Button btnCancel= (Button) dialogView.findViewById(R.id.btn_cancel);
            final AlertDialog alertDialog=builder.create();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = etPassword.getText().toString();
                    String confirmPassword=etConfirmPassword.getText().toString();
                    if (TextUtils.isEmpty(password)||TextUtils.isEmpty(confirmPassword)) {
                        Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    }else if (!password.equals(confirmPassword)){
                        Toast.makeText(HomeActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    } else {
                        mSharedPreferences.edit().putString(Constants.SharedPreferences.KEY_PASSWORD, Md5Utils.encode(password)).apply();
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, SafeActivity.class));
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }else{
            View dialogView=View.inflate(this,R.layout.dialog_input_password,null);
            builder.setView(dialogView);
            final EditText etPassword= (EditText) dialogView.findViewById(R.id.et_password);
            Button btnOK= (Button) dialogView.findViewById(R.id.btn_ok);
            Button btnCancel= (Button) dialogView.findViewById(R.id.btn_cancel);
            final AlertDialog alertDialog=builder.create();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = etPassword.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    } else if(!oldPassword.equals(Md5Utils.encode(password))){
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }else{
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, SafeActivity.class));
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

}
