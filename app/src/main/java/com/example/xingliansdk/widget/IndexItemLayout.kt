package com.example.xingliansdk.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.xingliansdk.R
import kotlinx.android.synthetic.main.layout_index_info.view.*

/**
 *
 *Created by frank on 2019/12/16
 */
class IndexItemLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    var mTypeText = ""
    var mTypeTextColor: Int = R.color.bottom_nav_text_dim
    var mContentText = ""
    var mContentTextColor: Int = R.color.bottom_nav_text_dim
    var mStyleImage: Int = R.mipmap.right_back
    var mTextSize = 0f
    var mContentTextSize = 0f

    var imageLeft: ImageView? = null
    var tvContent: TextView? = null
    var tvType: TextView? = null

    private fun initType(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val a =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.IndexItemLayout,
                defStyleAttr,
                defStyleRes
            )
        val n = a.indexCount
        for (i in 0 until n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.IndexItemLayout_contentText_index -> mContentText =
                    a.getString(attr).toString()
                R.styleable.IndexItemLayout_contentTextColor_index -> mContentTextColor =
                    a.getResourceId(attr, R.color.bottom_nav_text_dim)
                R.styleable.IndexItemLayout_tvTypeText_index -> mTypeText =
                    a.getString(attr).toString()
                R.styleable.IndexItemLayout_tvTypeColor_index -> mTypeTextColor =
                    a.getResourceId(attr, R.color.bottom_nav_text_dim)
                R.styleable.IndexItemLayout_TextSize_index -> mTextSize = a.getDimension(attr, 3f)
                R.styleable.IndexItemLayout_contentTextSize_index -> mContentTextSize =
                    a.getDimension(attr, 4f)
                R.styleable.IndexItemLayout_styleImage_index -> mStyleImage =
                    a.getResourceId(attr, mStyleImage)
            }
        }
        a.recycle()
    }

    init {
        /**填充布局 */
        initType(context, attrs, defStyleAttr, defStyleRes)
        val layout = View.inflate(getContext(), R.layout.layout_index_info, null)
        tvContent = layout.tvContent
        imageLeft = layout.img_left
        tvType = layout.tvType
        /**设置 */
        if (mTextSize == 0f) mTextSize = 14f
        if (mContentTextSize == 0f) mContentTextSize = 12f

        tvContent!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContentTextSize)
        tvContent!!.setTextColor(resources.getColor(mContentTextColor))
        tvContent!!.text = mContentText
        tvType!!.textSize = mTextSize
        tvType!!.setTextColor(resources.getColor(mTypeTextColor))
        tvType!!.text = mTypeText
        imageLeft!!.setImageDrawable(resources.getDrawable(mStyleImage))
        addView(layout)
    }

    fun setTyTypeContext(txt : String){
        tvType!!.text = txt
    }

    fun setContentText(contentText: String?) {
        tvContent!!.text = contentText
    }

    fun getContentText(): String? {
        return tvContent!!.text.toString().trim { it <= ' ' }
    }

    fun setTitleText(titleText: String?) {
        tvType!!.text = titleText
    }

    fun getTitleText(titleText: String?): String? {
        return tvType!!.text.toString().trim { it <= ' ' }
    }

    fun setImage(iconImage: Int) {
        imageLeft!!.setImageResource(iconImage)
    }


}
