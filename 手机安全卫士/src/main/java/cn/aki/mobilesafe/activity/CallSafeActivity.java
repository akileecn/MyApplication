package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.adapter.MyBaseAdapter;
import cn.aki.mobilesafe.bean.BlackNumber;
import cn.aki.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2015/12/22.
 * 通讯卫士
 */
public class CallSafeActivity extends Activity{
    /**
     * 加载完成
     */
    private static final int WHAT_LOAD_COMPLETED=1;
    /**
     * 无更多数据
     */
    private static final int WHAT_NO_MORE_DATA = 2;
    private ListView lvBlackList;   //黑名单列表
    private LinearLayout llLoading; //加载中图标
    private BlackNumberDao blackNumberDao;
    private List<BlackNumber> mBlackNumberList;  //黑名单数据
    private BaseAdapter mAdapter;
    private int mPageIndex=1;
    private int mPageSize=20;
    private boolean mIsNoMore=false;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_LOAD_COMPLETED:
                    llLoading.setVisibility(View.INVISIBLE);
                    mAdapter.notifyDataSetChanged();
                    break;
                case WHAT_NO_MORE_DATA:
                    llLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(CallSafeActivity.this,"无更多数据",Toast.LENGTH_SHORT).show();
                    mIsNoMore=true;
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    /**
     * 初始化UI
     */
    private void initUI(){
        setContentView(R.layout.activity_call_safe);
        lvBlackList= (ListView) findViewById(R.id.lv_black_list);
        llLoading= (LinearLayout) findViewById(R.id.ll_loading);
        //滚动到底端加载下一页的数据
        lvBlackList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //不再滚动时
                    case SCROLL_STATE_IDLE:
                        int position = lvBlackList.getLastVisiblePosition();
                        //到达底部
                        if (position == mBlackNumberList.size() - 1) {
                            loadData();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //添加窗口
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity.this);
                View view = View.inflate(CallSafeActivity.this, R.layout.dialog_add_black_number, null);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                final EditText etNumber = (EditText) view.findViewById(R.id.et_number);
                final CheckBox cbPhone = (CheckBox) view.findViewById(R.id.cb_phone);
                final CheckBox cbSms = (CheckBox) view.findViewById(R.id.cb_sms);
                //取消
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //确认
                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = etNumber.getText().toString().trim();
                        boolean isPhone = cbPhone.isChecked();
                        boolean isSms = cbSms.isChecked();
                        if (TextUtils.isEmpty(number)) {
                            Toast.makeText(CallSafeActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!isPhone && !isSms) {
                            Toast.makeText(CallSafeActivity.this, "请选择拦截类型", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //保存数据库
                        int mode = (isPhone ? 1 : 0) + (isSms ? 2 : 0);
                        blackNumberDao.add(number, mode);
                        //添加到list
                        BlackNumber blackNumber = new BlackNumber();
                        blackNumber.setNumber(number);
                        blackNumber.setMode(mode);
                        mBlackNumberList.add(0,blackNumber);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData(){
        blackNumberDao=new BlackNumberDao(this);
        mBlackNumberList=new ArrayList<>();
        mAdapter=new BlackListAdapter(mBlackNumberList);
        lvBlackList.setAdapter(mAdapter);
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        if(mIsNoMore){
            Toast.makeText(CallSafeActivity.this,"无更多数据",Toast.LENGTH_SHORT).show();
            return;
        }
        llLoading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                blackNumberList=dao.queryAll();
                List<BlackNumber> newDataList=blackNumberDao.queryPage(mPageIndex, mPageSize);
                if(newDataList.size()>0){
                    mBlackNumberList.addAll(newDataList);
                    mPageIndex++;
                    handler.sendEmptyMessage(WHAT_LOAD_COMPLETED);
                }else{
                    handler.sendEmptyMessage(WHAT_NO_MORE_DATA);
                }
            }
        }).start();
    }

    /**
     * 黑名单列表适配器
     */
    private class BlackListAdapter extends MyBaseAdapter<BlackNumber>{
        public BlackListAdapter(List<BlackNumber> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_black_number, null);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tvModeName = (TextView) convertView.findViewById(R.id.tv_mode_name);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BlackNumber blackNumber = list.get(position);
            holder.tvNumber.setText(blackNumber.getNumber());
            holder.tvModeName.setText(blackNumber.getModeName());
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blackNumberDao.delete(blackNumber.getNumber());
                    list.remove(blackNumber);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        /**
         * 黑名单条目viewHolder
         */
        private class ViewHolder{
            TextView tvNumber;
            TextView tvModeName;
            ImageView ivDelete;
        }
    }
}
