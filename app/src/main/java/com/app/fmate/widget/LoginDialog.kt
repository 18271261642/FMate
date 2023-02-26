package com.app.fmate.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.app.fmate.R


class LoginDialog : Dialog {

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    open class Builder {

        private var context: Context
        private var title: String? = null
        private var positiveButtonText: String? = null
        private var negativeButtonText: String? = null
        private var positiveTextColor: Int = -1
        private var negativeTextColor: Int = -1
        private var canceledOnTouchOutside: Boolean = true
        private var listener: OnDialogClickListener? = null
        private var message: String? = null
        private var titleIcon: Int = -1

        private var gravity: Int = MESSAGE_LEFT_GRAVITY

        companion object {
            val MESSAGE_LEFT_GRAVITY = 0
            val MESSAGE_CENTER_GRAVITY = 1
            val MESSAGE_RIGHT_GRAVITY = 2
            val MESSAGE_TOP_GRAVITY = 3
            val MESSAGE_BOTTOM_GRAVITY = 4
        }

        constructor(context: Context) {
            this.context = context
        }

        /**
         * 设置标题
         */
        fun setTitle(title: Any?): Builder {
            if (title is String) {
                this.title = title
            } else if (title is Int) {
                this.title = context!!.getString(title)
            }
            return this
        }

        /**
         * 设置显示的内容
         */
        fun setMessage(message: Any?): Builder {
            if (message is String) {
                this.message = message
            } else if (message is Int) {
                this.message = context.getString(message)
            }
            return this
        }

        /**
         * 设置是否可以外部点击失效
         */
        fun setCanceledOnTouchOutSide(status: Boolean): Builder {
            this.canceledOnTouchOutside = status
            return this
        }

        /**
         * 设置确定按钮的文案
         */
        fun setPositiveButton(msg: Any?): Builder {
            if (msg is String) {
                this.positiveButtonText = msg
            } else if (msg is Int) {
                this.positiveButtonText = context.getString(msg)
            }
            return this
        }

        /**
         * 设置确定按钮的颜色
         */
        fun setPositiveTextColor(@ColorInt color: Int): Builder {
            this.positiveTextColor = color
            return this
        }

        /**
         * 设置取消按钮的文案
         */
        fun setNegativeButton(msg: Any?): Builder {
            if (msg is String) {
                this.negativeButtonText = msg
            } else if (msg is Int) {
                this.negativeButtonText = context.getString(msg)
            }
            return this
        }

        /**
         * 设置取消按钮的颜色
         */
        fun setNegativeTextColor(@ColorInt color: Int): Builder {
            this.negativeTextColor = color
            return this
        }

        /**
         * 设置点击事件
         */
        fun setOnDialogClickListener(listener: OnDialogClickListener): Builder {
            this.listener = listener
            return this
        }

        /**
         * 设置内容剧中还是巨左
         */
        fun setGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        /**
         * 设置标题图标
         */
        fun setTitleIcon(icon: Int): Builder {
            this.titleIcon = icon
            return this
        }

        /**
         * 创建窗口
         */
        fun create(): LoginDialog {
            val inflater = LayoutInflater.from(context)
            val dialog = LoginDialog(context, R.style.Dialog)
            val view = inflater.inflate(R.layout.alert_dialog_login, null)
            val llTitle = view.findViewById<LinearLayout>(R.id.ll_title)
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
            val confirm = view.findViewById<TextView>(R.id.dialog_confirm)
            val cancel = view.findViewById<TextView>(R.id.dialog_cancel)
            val ivIcon = view.findViewById<ImageView>(R.id.iv_title_icon)


            dialogContent.text =
                if (message.isNullOrEmpty())
                    ""
                else
                    message
            confirm.text =
                if (positiveButtonText.isNullOrEmpty())
                    context.getString(R.string.text_sure)
                else
                    positiveButtonText
            cancel.text =
                if (negativeButtonText.isNullOrEmpty())
                    context.getString(R.string.text_cancel)
                else
                    negativeButtonText
            if (positiveTextColor != -1) {
                confirm.setTextColor(positiveTextColor)
            }
            if (negativeTextColor != -1) {
                cancel.setTextColor(negativeTextColor)
            }

            if (!positiveButtonText.isNullOrEmpty()) {
                confirm.visibility = VISIBLE
            } else {
                confirm.visibility = GONE
            }
            if (!negativeButtonText.isNullOrEmpty()) {
                cancel.visibility = VISIBLE
            } else {
                cancel.visibility = GONE
            }
            if (titleIcon == -1 && title.isNullOrEmpty()) {
                llTitle.visibility = GONE
            } else {
                llTitle.visibility = VISIBLE
                tvTitle.text =
                    if (title.isNullOrEmpty())
                        context.getString(R.string.fill_in_the_reason_for_the_inability_to_serve)
                    else
                        title
                if (titleIcon != -1) {
                    ivIcon.setImageResource(titleIcon)
                }
            }
            when (gravity) {
                MESSAGE_CENTER_GRAVITY -> {
                    dialogContent.gravity = Gravity.CENTER
                }
                MESSAGE_LEFT_GRAVITY -> {
                    dialogContent.gravity = Gravity.LEFT
                }
                MESSAGE_RIGHT_GRAVITY -> {
                    dialogContent.gravity = Gravity.RIGHT
                }
                MESSAGE_TOP_GRAVITY -> {
                    dialogContent.gravity = Gravity.TOP
                }
                MESSAGE_BOTTOM_GRAVITY -> {
                    dialogContent.gravity = Gravity.BOTTOM
                }
                else -> {
                    dialogContent.gravity = Gravity.LEFT
                }
            }

            if (listener != null) {
                confirm.setOnClickListener {
                    listener!!.onConfirmClick(dialog, DialogInterface.BUTTON_POSITIVE)
                }
                cancel.setOnClickListener {
                    listener!!.onCancelClick(dialog, DialogInterface.BUTTON_NEGATIVE)
                }
            }
            val window = dialog.window!!
            val rect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rect)
            view.minimumWidth = ((rect.width() * 0.8f).toInt())
            window.setGravity(Gravity.CENTER)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
            return dialog
        }

    }

    /**
     * call back of dialog btn
     */
    interface OnDialogClickListener {
        fun onConfirmClick(
            dialog: Dialog?,
            which: Int
        )

        fun onCancelClick(dialog: Dialog?, which: Int)
    }
}