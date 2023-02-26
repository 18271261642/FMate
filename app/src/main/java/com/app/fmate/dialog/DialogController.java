package com.app.fmate.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 功能：BaseDialog控制类
 */

class DialogController {
    private BaseDialog mDialog;
    private Window mWindow;
    private DialogViewHelper mViewHelper;



    public DialogController(BaseDialog dialog, Window window){
        this.mDialog = dialog;
        this.mWindow = window;
    }

    /**
     * 获取dialog
     * @return
     */
    public BaseDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取dialog的window
     * @return
     */
    public Window getWindow() {
        return mWindow;
    }

    /**
     * 设置辅助类
     * @param viewHelper
     */
    public void setViewHelper(DialogViewHelper viewHelper) {
        this.mViewHelper = viewHelper;
    }

    /**
     * 设置文字
     * @param viewId 资源Id
     * @param text 文本
     */
    public void setText(int viewId, CharSequence text) {
        mViewHelper.setText(viewId, text);
    }


    /**
     * 设置点击事件
     * @param viewId 资源Id
     * @param onClickListener 监听器
     */
    public void setOnClickListener(int viewId, DialogInterface.OnClickListener onClickListener) {
       mViewHelper.setOnClickListener(mDialog, viewId, onClickListener);
    }

    public void setViewVisible(int viewId, int visible){
        mViewHelper.setViewVisible(viewId, visible);
    }


    public static class DialogParams {
        public Context mContext;
        //style参数
        public int mThemeResId;
        //点击空白是否取消
        public boolean mCancelable = true;
        //布局视图
        public View mView;
        //布局资源文件
        public int mViewLayoutResId = 0;
        //文字
        public SparseArray<CharSequence> mTextArrays = new SparseArray<>();
        //点击事件监听
        public SparseArray<DialogInterface.OnClickListener> mClickArrays = new SparseArray<>();
        //显示和隐藏图标
        public SparseIntArray mVisibleArrays = new SparseIntArray();
        //create cancel 监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        //create dismiss 监听
        public DialogInterface.OnDismissListener mOnDismissListener;
        //create key 监听
        public DialogInterface.OnKeyListener mOnKeyListener;
        //dialog宽度
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        //dialog高度
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        //动画
        public int mAnimations = 0;
        //位置
        public int mGravity = Gravity.CENTER;


        public DialogParams(Context context) {
            this.mContext = context;
        }

        /**
         * 绑定和设置参数
         * @param mController create 控制器
         */
        public void apply(DialogController mController){
            //设置布局
            DialogViewHelper viewHelper= null;
            if (mViewLayoutResId != 0){
                viewHelper = new DialogViewHelper(mContext, mViewLayoutResId);
            }
            if (mView != null){
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView);
            }
            if (viewHelper == null){
                throw new IllegalArgumentException("请设置布局setContentView()！");
            }
            mController.getDialog().setContentView(viewHelper.getContentView());
            mController.setViewHelper(viewHelper);

            //设置文本
            int textArraySize = mTextArrays.size();
            for (int i = 0; i < textArraySize; i++) {
                viewHelper.setText(mTextArrays.keyAt(i), mTextArrays.valueAt(i));
            }

            //设置点击事件
            int clickArraySize = mClickArrays.size();
            for (int i = 0; i < clickArraySize; i++) {
                viewHelper.setOnClickListener(mController.mDialog, mClickArrays.keyAt(i), mClickArrays.valueAt(i));
            }

            //设置隐藏和显示
            int visibleSize = mVisibleArrays.size();
            for (int i = 0; i < visibleSize; i++){
                viewHelper.setViewVisible(mVisibleArrays.keyAt(i), mVisibleArrays.valueAt(i));
            }

            //配置自定义效果，全屏、从底部弹出、默认动画
            Window window = mController.getWindow();
            window.setGravity(mGravity);

            if(mAnimations != 0){
                window.setWindowAnimations(mAnimations);
            }

            //设置宽高
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            window.setAttributes(params);
        }

    }
}
