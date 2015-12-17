package cn.aki.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.aki.mobilesafe.db.dao.AddressDao;

/**
 * Created by Administrator on 2015/12/4.
 * 去电监听
 * 由AddressService$MyOutgoingReceiver代替
 */
@Deprecated
public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number=getResultData();
        String address=AddressDao.getAddress(number);
        Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
    }
}
