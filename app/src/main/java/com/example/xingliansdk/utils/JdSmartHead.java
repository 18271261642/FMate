//package com.example.xingliansdk.utils;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DecodeFormat;
//import com.example.xingliansdk.R;
//import com.scwang.smartrefresh.layout.api.RefreshHeader;
//
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
///**
// * @author ：shen
// * @date ：2021-07-16 11：25
// * @des ：
// */
//public class JdSmartHead extends LinearLayout implements RefreshHeader {
//
//    private TextView mHeaderText;
////    private TextView tvUpdate;
//    private ImageView mImageView;
//    CommonCustomClassicsHeaderBinding mBinding;
//
//    public static String REFRESH_HEADER_PULLING = null;//"下拉可以刷新";
//    public static String REFRESH_HEADER_REFRESHING = null;//"正在刷新...";
//    public static String REFRESH_HEADER_LOADING = null;//"正在加载...";
//    public static String REFRESH_HEADER_RELEASE = null;//"释放立即刷新";
//    public static String REFRESH_HEADER_FINISH = null;//"刷新完成";
//    public static String REFRESH_HEADER_FAILED = null;//"刷新失败";
//    public static String REFRESH_HEADER_UPDATE = null;//"上次更新 M-d HH:mm";
//
//
//    protected String mTextPulling="下拉可以刷新";//"下拉可以刷新";
//    protected String mTextRefreshing="正在刷新...";//"正在刷新...";
//    protected String mTextLoading="正在加载...";//"正在加载...";
//    protected String mTextRelease="释放立即刷新";//"释放立即刷新";
//    protected String mTextFinish="刷新完成";//"刷新完成";
//    protected String mTextFailed="刷新失败";//"刷新失败"
//    protected String mTextUpdate="上次更新 M-d HH:mm";;//"上次更新 M-d HH:mm";
//    protected String mTextSecondary="释放进入二楼";//"释放进入二楼";
//
//
//    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
//
//    protected Date mLastTime;
//    protected TextView mLastUpdateText;
//    protected SharedPreferences mShared;
//    protected DateFormat mLastUpdateFormat;
//    protected boolean mEnableLastTime = true;
//
//    public JdSmartHead(Context context) {
//        this(context, null);
//    }
//
//    public JdSmartHead(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public JdSmartHead(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setGravity(Gravity.CENTER);
////        setBackgroundColor(ContextCompat.getColor(context,R.color.frame_base_bule_028EF1));
////        View view = View.inflate(context, R.layout.common_custom_classics_header, this);
////        mHeaderText = view.findViewById(R.id.common_classics_title);
////        tvUpdate=view.findViewById(R.id.common_classics_update);
////        mImageView=view.findViewById(R.id.common_classics_progress);
//        mBinding = CommonCustomClassicsHeaderBinding.inflate(LayoutInflater.from(getContext()), this, true);
//        mHeaderText=mBinding.commonClassicsTitle;
//        mLastUpdateText=mBinding.commonClassicsUpdate;
//        mImageView=mBinding.commonClassicsProgress;
//
//
//        mTextUpdate = context.getString(R.string.srl_header_update);
//        mLastUpdateFormat = new SimpleDateFormat(mTextUpdate, Locale.getDefault());
//
//        KEY_LAST_UPDATE_TIME += context.getClass().getName();
//        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
//        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
//        init();
//    }
//
//    private void init(){
//        Glide.with(this)
//                .load(R.drawable.common_refresh_icon)
//                .apply(ImageLoaderUtil.displayImageOptions()
//                        .format(DecodeFormat.PREFER_ARGB_8888))
//                .into(mImageView);
//    }
//    public JdSmartHead setLastUpdateTime(Date time) {
//        final View thisView = this;
//        mLastTime = time;
//        mLastUpdateText.setText(mLastUpdateFormat.format(time));
//        if (mShared != null && !thisView.isInEditMode()) {
//            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
//        }
//        return this;
//    }
//
//    public JdSmartHead setTimeFormat(DateFormat format) {
//        mLastUpdateFormat = format;
//        if (mLastTime != null) {
//            mLastUpdateText.setText(mLastUpdateFormat.format(mLastTime));
//        }
//        return this;
//    }
//
//    public JdSmartHead setLastUpdateText(CharSequence text) {
//        mLastTime = null;
//        mLastUpdateText.setText(text);
//        return this;
//    }
//    /**
//     * 获取真实视图（必须返回，不能为null）
//     */
//    @NonNull
//    @Override
//    public View getView() {
//        return this;
//    }
//
//    /**
//     * 获取变换方式（必须指定一个：平移、拉伸、固定、全屏）
//     */
//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.Translate;
//    }
//
//    /**
//     * 设置主题颜色 （如果自定义的Header没有注意颜色，本方法可以什么都不处理）
//     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
//     */
//    @Override
//    public void setPrimaryColors(int... colors) {
//
//    }
//
//
//    /**
//     * 尺寸定义初始化完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
//     * @param kernel RefreshKernel 核心接口（用于完成高级Header功能）
//     * @param height HeaderHeight or FooterHeight
//     * @param maxDragHeight 最大拖动高度
//     */
//    @Override
//    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
//
//    }
//
//
//    /**
//     * 开始动画（开始刷新或者开始加载动画）
//     * @param refreshLayout RefreshLayout
//     * @param height HeaderHeight or FooterHeight
//     * @param maxDragHeight 最大拖动高度
//     */
//    @Override
//    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//
//    }
//
//    /**
//     * 动画结束
//     * @param refreshLayout RefreshLayout
//     * @param success 数据是否成功刷新或加载
//     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
//     */
//    @Override
//    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        LogUtils.e(success+"");
//        if (success) {
//            mHeaderText.setText(mTextFinish);
//            if (mLastTime != null) {
//                setLastUpdateTime(new Date());
//            }
//        } else {
//            mHeaderText.setText(mTextFailed);
//        }
//        return 500;
//    }
//
//
//    @Override
//    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
//
//    }
//
//    @Override
//    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//
//    }
//
//
//
//
//
//    @Override
//    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
//
//    }
//
//    @Override
//    public boolean isSupportHorizontalDrag() {
//        return false;
//    }
//
//    @Override
//    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//        LogUtils.e(newState+"");
//        switch (newState) {
//            case None:
//            case PullDownToRefresh:
//                mHeaderText.setText(mTextPulling);
////                mArrowView.setVisibility(VISIBLE);//显示下拉箭头
////                mProgressView.setVisibility(GONE);//隐藏动画
////                mArrowView.animate().rotation(0);//还原箭头方向
//                break;
//            case Refreshing:
//                mHeaderText.setText(mTextRefreshing);
////                mProgressView.setVisibility(VISIBLE);//显示加载动画
////                mArrowView.setVisibility(GONE);//隐藏箭头
//                break;
//            case ReleaseToRefresh:
//                mHeaderText.setText(mTextRelease);
////                mArrowView.animate().rotation(180);//显示箭头改为朝上
//                break;
//            case Loading:
//                mHeaderText.setText(mTextLoading);
//                break;
//        }
//    }
//}
