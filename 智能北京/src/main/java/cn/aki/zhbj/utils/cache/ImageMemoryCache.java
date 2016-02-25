package cn.aki.zhbj.utils.cache;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/2/25.
 * 图片内存缓存
 */
public class ImageMemoryCache extends MemoryCache<Bitmap> {

    @Override
    protected int sizeOfValue(Bitmap bitmap) {
        if(bitmap!=null){
            return bitmap.getByteCount();
        }else{
            return 0;
        }
    }

}
