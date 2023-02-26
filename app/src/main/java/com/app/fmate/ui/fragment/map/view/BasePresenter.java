package com.app.fmate.ui.fragment.map.view;


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class BasePresenter<V>{
    private boolean isUIEnable;

    public void setUIEnable(boolean UIEnable) {
        isUIEnable = UIEnable;
    }

    private Reference<V> mViewRef; //View接口类型的弱引用
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view); //建立关联
        onCreate();
    }

    protected V getView() {
        if(mViewRef == null)
            return null;
        return mViewRef.get(); //获取View
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null; //判断是否与View建立关联
    }


    /**
     * onDestroy
     */
    public void onDestroy(){

    }
    /**
     * onResume
     */
    public void onResume(){

    }
    /**
     * onCreateActivity
     */
    protected void onCreate(){

    }

    /**
     * onPause
     */
    public void onPause(){

    }
    /**
     * onStop
     */
    public void onStop(){

    }

    /**
     * 可见
     */
    public void onVisible(){

    }
    /**
     * 不可见
     */
    public void onInvisible(){

    }
    /**
     * UI是否可见
     */
    protected boolean isUIEnable(){
        return isUIEnable;
    }

    /**
     * 在V销毁的时候调用,解除绑定
     */
    public void onDetach() {
        if (mViewRef != null) {
            mViewRef.clear(); //解除关联
            mViewRef = null;
        }
    }

}
