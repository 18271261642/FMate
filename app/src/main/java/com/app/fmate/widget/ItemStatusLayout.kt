package com.app.fmate.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.app.fmate.R
import com.app.fmate.utils.HelpUtil
import kotlinx.android.synthetic.main.item_home_status.view.*

/**
 *
 *Created by frank on 2019/12/16
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ItemStatusLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    var mTitleText = ""
    var mTitleTextColor: Int = R.color.main_text_color
    var mContentText = ""
    var mContentTextColor: Int = R.color.text_hint_color
    var tv_content: TextView? = null
    var tv_title: TextView? = null
    var mTextSize = 0f


    fun initType(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ItemStatusLayout, defStyleAttr, defStyleRes)
        val n = a.indexCount
        for (i in 0 until n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.ItemStatusLayout_StatusTitleText -> mTitleText = a.getString(attr).toString()
                R.styleable.ItemStatusLayout_StatusTContentText -> mContentText = a.getString(attr).toString()
            }
        }
        a.recycle()
    }

    init {
        /**填充布局 */
        initType(context, attrs, defStyleAttr, defStyleRes)
       val layout = View.inflate(getContext(), R.layout.item_home_status, null)
        tv_title = layout.tvItemStatusTitle
        tv_content = layout.tvItemStatusSubTitle
        layout.setBackgroundColor(resources.getColor(R.color.white))
        /**设置 */
        if (mTextSize == 0f) mTextSize =HelpUtil.px2sp(context,14f)
        tv_title!!.textSize = HelpUtil.px2sp(context,mTextSize)
        tv_title!!.setTextColor( resources.getColor(mTitleTextColor))
        tv_title!!.text = mTitleText
        tv_content!!.textSize = HelpUtil.px2sp(context,mTextSize)
        tv_content!!.setTextColor( resources.getColor(mContentTextColor))
        tv_content!!.text = mContentText
        //       setBackgroundResource(mBackSelector);
        addView(layout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    fun setContentText(contentText: String?) {
        tv_content!!.text = contentText
    }

    fun getContentText(): String? {
        return tv_content!!.text.toString().trim { it <= ' ' }
    }

    fun setTitleText(titleText: String?) {
        tv_title!!.text = titleText
    }
    fun getTitleText(titleText: String?): String? {
        return tv_title!!.text.toString().trim { it <= ' ' }
    }
}
