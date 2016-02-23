package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.data.response.NewsData;
import cn.aki.zhbj.utils.CacheUtils;

/**
 * Created by Administrator on 2016/2/23.
 * 组图详情页
 */
public class PhotoDetailPage extends BaseDetailPage {
    private Button btnShowType;
    private List<NewsData.ListNews> mPhotoList;
    private BaseAdapter mAdapter;
    private ListView lvPhoto;
    private GridView gvPhoto;
    private final int[] mIcons={R.drawable.icon_pic_list_type,R.drawable.icon_pic_grid_type};
    private ImageOptions mImageOptions;
    private SharedPreferences mPref;

    public PhotoDetailPage(Context context, Categories.Menu data,Button showType) {
        super(context, data);
        btnShowType=showType;
    }

    @Override
    protected void initView() {
        mPref=C.getConfig(mContext);
        mImageOptions=new ImageOptions.Builder()
                .setLoadingDrawableId(R.drawable.topnews_item_default)
                .setFailureDrawableId(R.drawable.topnews_item_default)
                .build();
        mRootView= View.inflate(mContext, R.layout.detail_page_photo, null);
        lvPhoto= (ListView) mRootView.findViewById(R.id.lv_photo);
        gvPhoto= (GridView) mRootView.findViewById(R.id.gv_photo);
        btnShowType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片显示方式切换
                boolean isShowList=mPref.getBoolean(C.Sp.KEY_PHOTO_SHOW_TYPE,true);
                initShowType(!isShowList);
                mPref.edit().putBoolean(C.Sp.KEY_PHOTO_SHOW_TYPE,!isShowList).apply();
            }
        });
        initShowType(mPref.getBoolean(C.Sp.KEY_PHOTO_SHOW_TYPE,true));
        initData();
    }

    private void initShowType(boolean isShowList){
        lvPhoto.setVisibility(isShowList ? View.VISIBLE : View.INVISIBLE);
        gvPhoto.setVisibility(isShowList ? View.INVISIBLE : View.VISIBLE);
        btnShowType.setBackgroundResource(mIcons[isShowList ? 0 : 1]);
    }

    @Override
    public void initData() {
        final String url=C.Url.PHOTO;
        RequestParams params=new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.saveCache(mContext, url, result);
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void parseData(String result) {
        Gson gson=new Gson();
        NewsData newsData=gson.fromJson(result.replaceAll(C.Url.DEPRECATED_BASE,C.Url.BASE),NewsData.class);
        mPhotoList=newsData.data.news;
        if(mPhotoList!=null&&mPhotoList.size()>0){
            mAdapter=new MyPhotoAdapter();
            lvPhoto.setAdapter(mAdapter);
            gvPhoto.setAdapter(mAdapter);
        }
    }

    private class MyPhotoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public NewsData.ListNews getItem(int position) {
            return mPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=View.inflate(mContext,R.layout.item_photo,null);
                viewHolder=new ViewHolder();
                viewHolder.ivImage= (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            NewsData.ListNews news=getItem(position);
            viewHolder.tvTitle.setText(news.title);
            x.image().bind(viewHolder.ivImage,news.listimage,mImageOptions);
            return convertView;
        }

        private class ViewHolder{
            private ImageView ivImage;
            private TextView tvTitle;
        }
    }

}
