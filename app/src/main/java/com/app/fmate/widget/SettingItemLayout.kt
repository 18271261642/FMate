package com.app.fmate.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.app.fmate.R
import com.app.fmate.utils.HelpUtil
import kotlinx.android.synthetic.main.layout_setting_info.view.*

/**
 *
 *Created by frank on 2019/12/16
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SettingItemLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    var mTitleText = ""
    var mTitleTextColor: Int = R.color.black
    var mContentText = ""
    var mContentTextColor: Int = R.color.sub_text_color
    var mIconImage: Int = R.mipmap.right_back
    var mLayoutType = 0
    var tv_content: TextView? = null
    var tv_title: TextView? = null
    var iv_style_image: ImageView? = null
    var img_dian: ImageView? = null
    var iv_image: ImageView
    var rl_img: RelativeLayout? = null
    var mStyleImage: Int = R.mipmap.right_back
    val PADDING_LEFT = 15
    val PADDING = 0
    val LAYOUT_TYPE_B = 2
    val LAYOUT_TYPE_C = 3
    val LAYOUT_TYPE_D = 4
    val LAYOUT_TYPE_E = 5
    val LAYOUT_TYPE_F = 6
    val LAYOUT_TYPE_G = 7
    var mTextSize = 0f
    var mContentTextSize=0f

    fun initType(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val a =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SettingItemLayout,
                defStyleAttr,
                defStyleRes
            )
        val n = a.indexCount
        for (i in 0 until n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.SettingItemLayout_titleText -> mTitleText = a.getString(attr).toString()
                R.styleable.SettingItemLayout_mtitleTextColor -> mTitleTextColor =
                    a.getResourceId(attr, R.color.color_F1F4F4)
                R.styleable.SettingItemLayout_contentText -> mContentText =
                    a.getString(attr).toString()
                R.styleable.SettingItemLayout_contentTextColor -> mContentTextColor =
                    a.getResourceId(attr, R.color.color_F1F4F4)
                R.styleable.SettingItemLayout_styleImage -> mStyleImage =
                    a.getResourceId(attr, mStyleImage)
                R.styleable.SettingItemLayout_iconImage -> mIconImage =
                    a.getResourceId(attr, mIconImage)
                R.styleable.SettingItemLayout_layoutType -> mLayoutType = a.getInt(attr, 1)
                R.styleable.SettingItemLayout_TextSize -> mTextSize = a.getDimension(attr, 4f)
                R.styleable.SettingItemLayout_contentTextSize->mContentTextSize=a.getDimension(attr,3f)
            }
        }
        a.recycle()
    }

    init {
        /**填充布局 */
        initType(context, attrs, defStyleAttr, defStyleRes)
        val layout = View.inflate(getContext(), R.layout.layout_setting_info, null)
        tv_title = layout.tv_title
        iv_style_image = layout.iv_style_image
        img_dian = layout.img_dian
        iv_image = layout.iv_image
        rl_img = layout.rl_img
        tv_content = layout.tv_content
        /**设置 */
        if (mTextSize == 0f) mTextSize = 14f
        if (mContentTextSize == 0f) mContentTextSize = 12f
        tv_title!!.textSize = mTextSize
        tv_title!!.setTextColor(resources.getColor(mTitleTextColor))
        tv_title!!.text = mTitleText
        tv_content!!.setTextSize(TypedValue.COMPLEX_UNIT_SP,mContentTextSize)
//       TLog.error("mContentTextSize==$mContentTextSize  mTextSize==$mTextSize")
        tv_content!!.setTextColor(resources.getColor(mContentTextColor))
        tv_content!!.text = mContentText
        iv_image!!.setImageDrawable(resources.getDrawable(mIconImage))
        iv_style_image!!.setImageDrawable(resources.getDrawable(mStyleImage))
        rl_img!!.visibility = View.GONE
        //       setBackgroundResource(mBackSelector);
        when (mLayoutType) {
            LAYOUT_TYPE_B -> {
                /**类型2,左侧有文字,右侧无文字 */
                /**类型2,左侧有文字,右侧无文字 */
                /**类型2,左侧有文字,右侧无文字 */
                tv_content!!.visibility = View.GONE
                rl_img!!.visibility = View.GONE
                //rbtnOK.setVisibility(View.GONE);
            }
            LAYOUT_TYPE_C -> {
                /**类型3,左侧有图片,左侧有文字,右侧无文字 */
                /**类型3,左侧有图片,左侧有文字,右侧无文字 */
                /**类型3,左侧有图片,左侧有文字,右侧无文字 */
                iv_style_image!!.visibility = View.VISIBLE
                tv_title!!.setPadding(
                    HelpUtil.dip2px(getContext(), PADDING_LEFT.toFloat()),
                    PADDING,
                    PADDING,
                    PADDING
                )
                tv_content!!.visibility = View.GONE
                rl_img!!.visibility = View.VISIBLE
                iv_image!!.visibility = View.VISIBLE
            }
            LAYOUT_TYPE_D -> {
                /**类型4,左侧有图片,左侧右侧有文字 */
                /**类型4,左侧有图片,左侧右侧有文字 */
                /**类型4,左侧有图片,左侧右侧有文字 */
                rl_img!!.visibility = View.VISIBLE
                iv_style_image!!.visibility = View.VISIBLE
                tv_title!!.setPadding(
                    HelpUtil.dip2px(getContext(), PADDING_LEFT.toFloat()),
                    PADDING,
                    PADDING,
                    PADDING
                )
            }
            LAYOUT_TYPE_E -> {
                iv_style_image!!.visibility = View.VISIBLE
                tv_title!!.setPadding(
                    HelpUtil.dip2px(getContext(), PADDING_LEFT.toFloat()),
                    PADDING,
                    PADDING,
                    PADDING
                )
                iv_image.visibility = View.INVISIBLE
                rl_img!!.visibility = View.INVISIBLE
            }
            LAYOUT_TYPE_F -> {
                iv_style_image!!.visibility = View.GONE
                tv_title!!.setPadding(
                    HelpUtil.dip2px(getContext(), PADDING_LEFT.toFloat()),
                    PADDING,
                    PADDING,
                    PADDING
                )
                iv_image.visibility = View.GONE
                rl_img!!.visibility = View.GONE
            }
            LAYOUT_TYPE_G -> {
                iv_style_image!!.visibility = View.INVISIBLE
                tv_title!!.setPadding(
                    HelpUtil.dip2px(getContext(), PADDING_LEFT.toFloat()),
                    PADDING,
                    PADDING,
                    PADDING
                )
                iv_image.visibility = View.INVISIBLE
                rl_img!!.visibility = View.INVISIBLE
                ll_top!!.left = 10
            }
        }
        addView(layout)
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

    fun setImage(iconImage: Int) {
        iv_style_image!!.setImageResource(iconImage)
    }

    fun setRightImage(iconImage: Int)
    {
        iv_image?.setImageResource(iconImage)
    }
    fun setDian(istrue: Boolean) {
        if (istrue) img_dian!!.visibility = View.VISIBLE else img_dian!!.visibility = View.GONE
    }


}
