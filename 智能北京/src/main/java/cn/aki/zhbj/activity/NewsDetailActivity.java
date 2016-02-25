package cn.aki.zhbj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import cn.aki.zhbj.R;
import cn.aki.zhbj.common.C;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2016/2/22.
 * 新闻详情页
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener{
    private static final String TAG=NewsDetailActivity.class.getName();
    private WebView wvContent;
    private WebSettings mSettings;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShareSDK.initSDK(this);
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        mPref=C.getConfig(this);
        setContentView(R.layout.activity_news_detail);
        /**网页*/
        wvContent= (WebView) findViewById(R.id.wv_content);
        mSettings=wvContent.getSettings();
        mSettings.setJavaScriptEnabled(true);
        setSettingTextZoom(mSettings);
        wvContent.setWebViewClient(new WebViewClient(){
            /**
             * 页面开始加载
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "加载页面->" + url);
            }

            /**
             * 页面加载完成
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "页面加载完成->" + url);
            }

            /**
             * 页面中连接的跳转
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "连接跳转->" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        /**菜单按钮*/
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ImageView ivShare = (ImageView) findViewById(R.id.iv_share);
        ImageView ivTextSize = (ImageView) findViewById(R.id.iv_text_size);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivTextSize.setOnClickListener(this);
    }

    /**
     * 设置网页字体大小
     */
    private void setSettingTextZoom(WebSettings settings){
        int textSize = mPref.getInt(C.Sp.KEY_WEB_TEXT_SIZE, 2);
        int[] TEXT_ZOOMS = {200, 150, 100, 75, 50};
        settings.setTextZoom(TEXT_ZOOMS[textSize]);
    }

    private void initData(){
        String url=getIntent().getStringExtra(C.Extra.URL);
        wvContent.loadUrl(url);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_text_size:
                showTextSizeDialog();
                break;
            case R.id.iv_share:
                showShare();
                break;
            default:
                break;
        }
    }

    /**
     * 显示设置文本大小对话框
     */
    private void showTextSizeDialog(){
        int textSize=mPref.getInt(C.Sp.KEY_WEB_TEXT_SIZE, 2);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"超大字体", "大字体", "正常", "小字体", "超小字体"}, textSize, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                C.getConfig(NewsDetailActivity.this).edit().putInt(C.Sp.KEY_WEB_TEXT_SIZE, which).apply();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSettingTextZoom(mSettings);
            }
        });
        builder.show();
    }

    /**
     * 分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("测试标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本，啦啦啦~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }
}
