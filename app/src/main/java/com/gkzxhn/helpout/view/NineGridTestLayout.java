package com.gkzxhn.helpout.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gkzxhn.helpout.utils.ImageUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * @classname：NineGridTestLayout
 * @author：liushaoxiang
 * @date：2019/4/18 2:29 PM
 * @description：
 */
public class NineGridTestLayout extends NineGridLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, String url, final int parentWidth) {

        ImageUtils.INSTANCE.displayImage(getMContext(), imageView, url, ImageUtils.INSTANCE.getPhotoImageOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int newW;
                int newH;
                if (h > w * MAX_W_H_RATIO) {
                    //h:w = 5:3
                    newW = parentWidth / 2;
                    newH = newW * 5 / 3;
                } else if (h < w) {
                    //h:w = 2:3
                    newW = parentWidth * 2 / 3;
                    newH = newW * 2 / 3;
                } else {
                    //newH:h = newW :w
                    newW = parentWidth / 2;
                    newH = h * newW / w;
                }
                setOneImageLayoutParams(imageView, newW, newH);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return false;
    }

    @Override
    protected void displayImage(RatioImageView imageView, String url) {
        Glide.with(mContext).load(url).into(imageView);
//        ImageLoader.getInstance().displayImage(url, imageView, ImageUtils.INSTANCE.getPhotoImageOption());
    }

    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
        Toast.makeText(getMContext(), "点击了图片" + url, Toast.LENGTH_SHORT).show();
    }
}
