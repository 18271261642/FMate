package com.example.xingliansdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 功能:dialog辅助工具类
 */

class DialogViewHelper {
    //视图布局
    private View mContentView = null;

    private SparseArray<WeakReference<View>> mViews;

    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    public DialogViewHelper(Context context, int mViewLayoutResId){
       this();
       mContentView = LayoutInflater.from(context).inflate(mViewLayoutResId, null);
    }

    /**
     * 设置文字
     * @param viewId 资源Id
     * @param text 文本
     */
    public void setText(int viewId, CharSequence text) {
        TextView textView = getViewById(viewId);
        if (textView != null){
            textView.setText(text);
        }
    }


    /**
     * 设置点击事件
     * @param viewId 资源Id
     * @param onClickListener 监听器
     */
    public void setOnClickListener(final BaseDialog dialog, final int viewId, final DialogInterface.OnClickListener onClickListener) {
        View view = getViewById(viewId);
        if (view != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(dialog, view.getId());
                }
            });
        }
    }

    /**
     * 设置是否显示或隐藏视图
     * @param viewId 资源Id
     * @param visible 显示或隐藏
     */
    public void setViewVisible(int viewId, int visible){
        View view = getViewById(viewId);
        if (view != null){
            view.setVisibility(visible);
        }
    }

    public <V extends View>V getViewById(int viewId){
        WeakReference<View> viewReference =  mViews.get(viewId);
        View view = null;
        if (viewReference != null){
            view = viewReference.get();
        }
        if (view == null){
            view = mContentView.findViewById(viewId);
            if (view != null){
                mViews.put(viewId, new WeakReference<>(view));
            }
        }

        return (V)view;
    }

    public void setContentView(View view){
        this.mContentView = view;
    }

    public View getContentView() {
        return mContentView;
    }
}
