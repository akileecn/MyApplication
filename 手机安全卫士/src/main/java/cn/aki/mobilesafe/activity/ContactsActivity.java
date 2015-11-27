package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.aki.mobilesafe.R;
import static android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * Created by Administrator on 2015/11/27.
 * 联系人
 */
public class ContactsActivity extends Activity{
    private static final String KEY_NAME="name";
    private static final String KEY_PHONE="phone";

    public static final String EXTRA_KEY=KEY_PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ListView lvContacts= (ListView) findViewById(R.id.lv_contacts);
        lvContacts.setAdapter(new SimpleAdapter(this,getContactsList(),R.layout.contact_item,new String[]{KEY_NAME,KEY_PHONE},new int[]{R.id.tv_name,R.id.tv_phone}));
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvPhone= (TextView) view.findViewById(R.id.tv_phone);
                String phone=tvPhone.getText().toString();
                //回传数据
                Intent intent=new Intent();
                intent.putExtra(EXTRA_KEY,phone);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private List<Map<String,String>> getContactsList(){
        List<Map<String,String>> contactsList=new ArrayList<>();    //jdk1.7特性
        Cursor cursor=getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.DISPLAY_NAME,Phone.NUMBER},null, null, null);
        //异常时返回null
        if(cursor!=null){
            while(cursor.moveToNext()){
                Map<String,String> contactMap=new HashMap<>();
                contactMap.put(KEY_NAME, cursor.getString(0));
                contactMap.put(KEY_PHONE,cursor.getString(1).replaceAll("[\\s-]", ""));
                contactsList.add(contactMap);
            }
            cursor.close();
        }
        return contactsList;
    }
}
