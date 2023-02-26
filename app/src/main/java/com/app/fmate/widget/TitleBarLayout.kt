package com.app.fmate.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.app.fmate.R
import com.app.fmate.utils.HelpUtil

/**
 *
 *Created by frank on 2020/1/9
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
open class TitleBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val DEFAULT_BANK_ICON: Int =  R.mipmap.icon_arrow_left
    private val DEFAULT_TEXT_COLOR = Color.WHITE
    protected val DEFAULT_BACKRESOURCE: Int =  R.drawable.ka_action_click_selector
    var mContext: Context = context
    var showHome = true
    protected var homeBackClick = false
    protected var showTitle = true
    protected var showAction = false
    protected var showActionImage = false
    protected var textColor = resources.getColor(R.color.white)
    protected var actionTextColor = resources.getColor(R.color.white)
    protected var mActionTextSize = 0f
    protected var mTitle: String? = null
    protected var mActionText: String? = null

    private var iconHome = DEFAULT_BANK_ICON
    private var iconActionImage: Int = R.mipmap.right_back
    private var mBackgroundColor: Int = R.color.white

    private val TITLE_BACK_ID = 0x1000
    private val ACTION_ID = 0x1001
    private val ACTION_IMAGE_ID = 0x1002
      var mBackLayout: RelativeLayout? = null
      var mTitleLayout: RelativeLayout? = null
      var mActionLayout: RelativeLayout? = null
      var mActionImageLayout: RelativeLayout? = null

      var mTitleTextView: TextView? = null
      var action: TextView? = null
      var actionImage: ImageView? = null
    var back: ImageView? = null
    private var mListener: TitleBarListener? =
        null

    var mViewClickListener: ViewClickListener =
        ViewClickListener()

    protected fun initType(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val a = context.theme
            .obtainStyledAttributes(attrs, R.styleable.TitleBarLayout, defStyleAttr, defStyleRes)
        val n = a.indexCount
        for (i in 0 until n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.TitleBarLayout_showHome -> showHome = a.getBoolean(attr, true)
                R.styleable.TitleBarLayout_showAction -> showAction = a.getBoolean(attr, false)
                R.styleable.TitleBarLayout_showActionImage -> showActionImage =
                    a.getBoolean(attr, false)
                R.styleable.TitleBarLayout_iconHome -> iconHome =
                    a.getResourceId(attr, DEFAULT_BANK_ICON)
                R.styleable.TitleBarLayout_iconActionImage -> iconActionImage =
                    a.getResourceId(attr, DEFAULT_BANK_ICON)
                R.styleable.TitleBarLayout_backcolor -> mBackgroundColor =
                    a.getResourceId(attr, R.color.white)
                R.styleable.TitleBarLayout_midtitle -> mTitle = a.getString(attr)
                R.styleable.TitleBarLayout_actionText -> mActionText = a.getString(attr)
                R.styleable.TitleBarLayout_homeBackClick -> homeBackClick =
                    a.getBoolean(attr, false)
                R.styleable.TitleBarLayout_titleTextColor01 -> textColor =
                    a.getColor(attr, resources.getColor(R.color.color_F1F4F4))
                R.styleable.TitleBarLayout_actionTextColor -> actionTextColor =
                    a.getColor(attr, resources.getColor(R.color.color_F1F4F4))
                R.styleable.TitleBarLayout_actionTextSize -> mActionTextSize =
                    a.getDimension(attr, 4f)
            }
        }
        a.recycle()
    }

    init {
        initType(context, attrs, defStyleAttr, defStyleRes)
        gravity = Gravity.CENTER_VERTICAL
        if (showHome) {
            addView(addHomeBackView())
        }
        if (showActionImage) {
            addView(addActionImageView())
        }
        if (showAction) {
            addView(addActionView())
        }
        if (showTitle) {
            addView(addTitleView())
        }
        setBackgroundResource(mBackgroundColor)
    }

    private fun addHomeBackView(): View? {
        mBackLayout = RelativeLayout(context)
        val lp = LayoutParams(
            HelpUtil.dip2px(context, 44F),
            LayoutParams.MATCH_PARENT
        )
        lp.addRule(ALIGN_PARENT_LEFT, 1)
        mBackLayout!!.id = TITLE_BACK_ID
        mBackLayout!!.setBackgroundResource(DEFAULT_BACKRESOURCE)
        mBackLayout!!.layoutParams = lp
        mBackLayout!!.setOnClickListener(mViewClickListener)
        back = ImageView(context)
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        param.addRule(CENTER_IN_PARENT)
        back!!.layoutParams = param
        back!!.setImageResource(iconHome)
        mBackLayout!!.addView(back)
        // mBackLayout.setVisibility(GONE);
        return mBackLayout
    }

    private fun addActionView(): View? {
        mActionLayout = RelativeLayout(context)
        val lp = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        if (null == mActionImageLayout) {
            lp.addRule(ALIGN_PARENT_RIGHT)
        } else {
            lp.addRule(LEFT_OF, mActionImageLayout!!.id)
        }
        mActionLayout!!.id = ACTION_ID
        mActionLayout!!.setBackgroundResource(DEFAULT_BACKRESOURCE)
        mActionLayout!!.layoutParams = lp
        if (showAction) {
            mActionLayout!!.visibility = View.VISIBLE
        } else {
            mActionLayout!!.visibility = View.GONE
        }
        mActionLayout!!.setOnClickListener(mViewClickListener)
        action = TextView(context)
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        param.addRule(CENTER_IN_PARENT)

        action!!.layoutParams = param
        action!!.setPadding(HelpUtil.dip2px(context, 16F), 0, HelpUtil.dip2px(context, 16.toFloat()), 0)
        action!!.setSingleLine()
        if (mActionTextSize == 0f) mActionTextSize = 14f
        action!!.textSize = mActionTextSize
        action!!.gravity = CENTER_IN_PARENT
        action!!.setTextColor(actionTextColor)
        if (!TextUtils.isEmpty(mActionText)) {
            action!!.text = mActionText
        }
        action!!.maxLines=1
        action!!.isSingleLine = true
        mActionLayout!!.addView(action)
        return mActionLayout
    }

    private fun addActionImageView(): View? {
        mActionImageLayout = RelativeLayout(context)
        val lp = LayoutParams(
            HelpUtil.dip2px(context, 44F),
            LayoutParams.MATCH_PARENT
        )
        lp.addRule(ALIGN_PARENT_RIGHT)
        mActionImageLayout!!.id = ACTION_IMAGE_ID
        mActionImageLayout!!.setBackgroundResource(DEFAULT_BACKRESOURCE)
        mActionImageLayout!!.layoutParams = lp
        if (showActionImage) {
            mActionImageLayout!!.visibility = View.VISIBLE
        } else {
            mActionImageLayout!!.visibility = View.GONE
        }
        mActionImageLayout!!.setOnClickListener(mViewClickListener)
        actionImage = ImageView(context)
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        param.addRule(CENTER_IN_PARENT)
        actionImage!!.layoutParams = param
        actionImage!!.setImageResource(iconActionImage)
        mActionImageLayout!!.addView(actionImage)
        return mActionImageLayout
    }

    private fun addTitleView(): View? {
        mTitleLayout = RelativeLayout(context)
        if (null == mActionImageLayout || mActionImageLayout!!.visibility == View.GONE) {
            resetTitleBarTitleParams(false)
        } else {
            resetTitleBarTitleParams()
        }
        mTitleTextView = TextView(context)
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        param.addRule(CENTER_IN_PARENT)
        mTitleTextView!!.layoutParams = param
        mTitleTextView!!.maxWidth = HelpUtil.dip2px(context, 220F)
        mTitleTextView!!.textSize = 18f
        mTitleTextView!!.marqueeRepeatLimit = -1
        mTitleTextView!!.gravity = CENTER_HORIZONTAL
        mTitleTextView!!.setTextColor(textColor)
        mTitleTextView!!.text = mTitle
        mTitleTextView!!.maxLines=1
        mTitleTextView!!.isSingleLine = true
        val tp = mTitleTextView!!.paint //加粗
        tp.isFakeBoldText = true
        // mTitleTextView.getPaint();
        mTitleLayout!!.addView(mTitleTextView)
        return mTitleLayout
    }

    fun setTitleText(text: String?) {
        if (null != mTitleTextView) {
            mTitleTextView!!.text = text
        }
    }

    fun setTitleText(resId: Int) {
        if (null != mTitleTextView) {
            mTitleTextView!!.text = context.getString(resId)
        }
    }

    fun showActionButton(showAction: Boolean) {
        if (null != mActionLayout) {
            if (showAction) {
                mActionLayout!!.visibility = View.VISIBLE
            } else {
                mActionLayout!!.visibility = View.GONE
            }
        }
    }

    fun showActionImageButton(showActionImage: Boolean) {
        if (null != mActionImageLayout) {
            if (showActionImage) {
                mActionImageLayout!!.visibility = View.VISIBLE
            } else {
                mActionImageLayout!!.visibility = View.GONE
            }
        }
    }

    /**
     * 右上角文字按钮设置
     */
    fun setActionText(text: String?) {
        if (showAction && null != action) {
            action!!.text = text
        }
    }

    /**
     * 右上角文字按钮设置
     */
    fun setActionText(res: Int) {
        if (showAction && null != action) {
            action!!.text = context.getString(res)
        }
    }

    /**
     * 右上角图片按钮
     */
    fun setActionImage(res: Int) {
        if (showAction && null != action) {
            actionImage!!.setImageResource(iconActionImage)
        }
    }

    /**
     * 是否显示
     */
    fun showActionImage(status: Boolean) {
            if (status) {
                actionImage?.visibility = View.VISIBLE
            }
            else {
                actionImage?.visibility = View.GONE
            }
    }

    /**
     * 右上角图片按钮
     */
    fun BackImage(res: Int) {
        back!!.setImageResource(iconHome)
    }

   inner class ViewClickListener : OnClickListener {
        override fun onClick(v: View) {

            if (v.id === mBackLayout?.id) {
                if (!homeBackClick) {
                    if (mListener != null) {
                        mListener?.onBackClick()
                    }
                } else {
                    (context as Activity).onBackPressed()
                    (context as Activity).finish()
                    mBackLayout
                }
            } else if (v ===mActionLayout) {
                if ( mListener != null) {
                    mListener?.onActionClick()
                }
            } else if (v ===  mActionImageLayout) {
                if ( mListener != null) {
                    mListener?.onActionImageClick()
                }
            }
        }
    }


    fun setTitleBarListener(l: TitleBarListener) {
        mListener = l
    }

    //显示返回按钮
    fun showTitleBarHomeIcon() {
        if (null != mBackLayout) {
            mBackLayout!!.visibility = View.VISIBLE
        }
    }

    //隐藏返回按钮
    fun hideTitleBarHomeIcon(b: Boolean) {
        if (null != mBackLayout) {
            mBackLayout!!.visibility = View.GONE
        }
    }

    /**
     * 关闭按钮是否显示时，调整Title的宽度
     */
    private fun resetTitleBarTitleParams() {
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        var margin: Int
        margin =
            if ((null == mActionImageLayout || mActionImageLayout!!.visibility == View.GONE) && (null == mActionLayout || mActionLayout!!.visibility == View.GONE)) {
                44
            } else {
                80
            }
        lp.setMargins(
            HelpUtil.dip2px(context, margin.toFloat()),
            0,
            HelpUtil.dip2px(context, margin.toFloat()),
            0
        )
        mTitleLayout!!.layoutParams = lp
    }

    /**
     * 强制设置Title的宽度，当返回键不显示时，左边最小的间隔为44dp
     *
     * @param b
     */
    private fun resetTitleBarTitleParams(b: Boolean) {
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        val margin = if (b) 80 else 44
        lp.setMargins(
            HelpUtil.dip2px(context, margin.toFloat()),
            0,
            HelpUtil.dip2px(context, margin.toFloat()),
            0
        )
        mTitleLayout!!.layoutParams = lp
    }

    interface TitleBarListener {
        fun onBackClick()
        fun onActionImageClick()
        fun onActionClick()
    }


    abstract class BaseTitleBarListener : TitleBarListener {
        override fun onBackClick() {}
        override fun onActionImageClick() {}
        override fun onActionClick() {}
    }
}