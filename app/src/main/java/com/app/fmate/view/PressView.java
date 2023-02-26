package com.app.fmate.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.fmate.R;

/**
 * Created by Admin
 * Date 2021/9/10
 */
public class PressView extends LinearLayout {

    private boolean isShowProgress;
    private String strStartText;
    private String strEndText;
    private String strEndTipsText;
    private int progressColor;


    private ObjectAnimator animator;

    private OnSportEndViewOnclick onSportEndViewOnclick;

    public void setOnSportEndViewOnclick(OnSportEndViewOnclick onSportEndViewOnclick) {
        this.onSportEndViewOnclick = onSportEndViewOnclick;
    }



    private TasksRopeCompletedView sport_stop ;
//    private TextView  tv_rope_start ;
//    private TextView tv_end_title;
//    private TextView tv_end_tips ;
//    private LinearLayout layout_end ;

    private int startTxtColor;
    private int circleColor;

    public PressView(Context context) {
        super(context);

    }

    public PressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public PressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PressView);
        isShowProgress = typedArray.getBoolean(R.styleable.PressView_AnimSporisShowProgress, true);
        strStartText = typedArray.getString(R.styleable.PressView_AnimSporShowStarttext);
        strEndText = typedArray.getString(R.styleable.PressView_AnimSporShowEndText);
        strEndTipsText = typedArray.getString(R.styleable.PressView_AnimSporShowEndTextTip);
        progressColor = typedArray.getColor(
                R.styleable.PressView_progressColor,
                Color.parseColor("#2F2F33"));

        startTxtColor = typedArray.getColor(R.styleable.PressView_start_txt_color,Color.WHITE);
        circleColor = typedArray.getColor(R.styleable.PressView_status_circle_color,Color.GREEN);
        typedArray.recycle();

        initViews();
    }

    private void initViews() {
        View view  = LayoutInflater.from(getContext()).inflate(R.layout.view_anim_end_layout, this, true);

        sport_stop = view.findViewById(R.id.sport_stop);
        //tv_rope_start = view.findViewById(R.id.tv_rope_start);

      //  tv_rope_start.setTextColor(Color.WHITE);
        sport_stop.setTxtColor(Color.WHITE);
        sport_stop.setmCircleColor(circleColor);
//        tv_end_title = view.findViewById(R.id.tv_end_title);
//        tv_end_tips = view.findViewById(R.id.tv_end_tips);
//        layout_end = view.findViewById(R.id.layout_end);

        if (isShowProgress) {
//            tv_rope_start.setVisibility(GONE);
//            tv_end_title.setText(strEndText);
//            tv_end_tips.setTag(strEndTipsText);
            sport_stop.setRingColor(progressColor);
            sport_stop.setTxtStr(strStartText);
        } else {

//            tv_rope_start.setVisibility(GONE);
//            tv_rope_start.setText(strStartText);

        }
//        sport_stop.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onSportEndViewOnclick != null) {
//                    onSportEndViewOnclick.onStartButton();
//                }
//            }
//        });

        initData();

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    boolean isUP = true;
    int mCurrentProgress = 0;

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {




        sport_stop.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               switch (motionEvent.getAction()){
                   case   MotionEvent.ACTION_DOWN : {
                       isUP = false;
                       mCurrentProgress = 0;
                       startAnimotion();
                      // return@OnTouchListener true
                   }
                   break;
                   case  MotionEvent.ACTION_UP : {
                       isUP = true;
                       if (animator != null) {
                           animator.cancel();
                       }
                   }
                   break;
               }

                return true;
            }
        });

    }

    private void startAnimotion() {
        if (sport_stop != null) {
            //  sportStop.setProgress(0);
            animator = ObjectAnimator.ofFloat(sport_stop, "progress", 0f, 100f);
            animator.setDuration(3000);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    sport_stop.setProgress(0f);
                    animator = null;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    sport_stop.setProgress(0f);
                    if(!isUP){
                        animator = null;
                        if (onSportEndViewOnclick != null) {
                            onSportEndViewOnclick.onProgressCompetly();
                        }
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationPause(Animator animation) {
                    super.onAnimationPause(animation);
                }
            });

        }
    }


    public interface OnSportEndViewOnclick {
        void onStartButton();
        void onProgressCompetly();
    }

    public void setEndText( String context,  String tips) {
      //  tv_rope_start.setVisibility(GONE);
//        layout_end.setVisibility(VISIBLE);
//        tv_end_title.setText(context);
//        tv_end_tips.setText(tips);
        //  sport_stop.setRingColor(progressColor)
    }

    public void setStartText(String content) {
       // strStartText = content;
//        layout_end.setVisibility(GONE);
      //  tv_rope_start.setVisibility(VISIBLE);
       // tv_rope_start.setText(content);
    }


    public void setCircleTxt(String txt){
       // tv_rope_start.setText(txt);

    }


    public void setCircleColor(int circleColor) {
        sport_stop.setmCircleColor(circleColor);

       // sport_stop.invalidate();

    }
    public void setCircleTextColor(int color){
      // tv_rope_start.setTextColor(color);
        sport_stop.setTxtColor(color);
       // invalidate();

    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
        invalidate();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(onSportEndViewOnclick != null)
                onSportEndViewOnclick.onStartButton();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }
}
