package com.gkzxhn.helpout.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * @classname：ObservableAlphaScrollView
 * @author：liushaoxiang
 * @date：2019/4/17 4:11 PM
 * @description：划动处理类
 */
public class ObservableAlphaScrollView extends NestedScrollView {
    private OnAlphaScrollChangeListener mOnAlphaScrollChangeListener;

    public ObservableAlphaScrollView(@NonNull Context context) {
        super(context);
    }

    public ObservableAlphaScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableAlphaScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnAlphaScrollChangeListener(OnAlphaScrollChangeListener onAlphaScrollChangeListener){
        this.mOnAlphaScrollChangeListener = onAlphaScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnAlphaScrollChangeListener != null){
            mOnAlphaScrollChangeListener.onVerticalScrollChanged(t);
        }
    }

    public interface OnAlphaScrollChangeListener{
        void onVerticalScrollChanged(int t);
    }
}
