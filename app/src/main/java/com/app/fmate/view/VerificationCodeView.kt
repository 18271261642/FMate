package com.app.fmate.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.fmate.R

class VerificationCodeView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private var view =
        LayoutInflater.from(context).inflate(R.layout.view_verification_code, this, true)
    private var tvFirst: TextView = view.findViewById(R.id.tvVerification1)
    private var tvSecond: TextView = view.findViewById(R.id.tvVerification2)
    private var tvThird: TextView = view.findViewById(R.id.tvVerification3)
    private var tvFourth: TextView = view.findViewById(R.id.tvVerification4)
    private var et: EditText = view.findViewById(R.id.etVerification)
    val codes = arrayListOf<String>()
    var block: ((Boolean) -> Unit)? = null

    init {
        setTextViews()

        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    et.setText("")
                    if (codes.size < 4) {
                        codes.add(s.toString())
                        setTextViews()
                    }
                }
                block?.invoke((codes.size == 4))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        et.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && codes.size > 0) {
                codes.removeAt(codes.size - 1)
                setTextViews()
                block?.invoke((codes.size == 4))
                true
            } else {
                false
            }
        }
    }

    private fun setTextViews() {
        setDefaultViews()
        codes.size.let {
            if (it >= 1) {
                tvFirst.text = codes[0]
            }
            if (it >= 2) {
                tvSecond.text = codes[1]
            }
            if (it >= 3) {
                tvThird.text = codes[2]
            }
            if (it >= 4) {
                tvFourth.text = codes[3]
            }

            when (it) {
                0 -> tvFirst.setBackgroundResource(R.drawable.bg_text_checked)
                1 -> tvSecond.setBackgroundResource(R.drawable.bg_text_checked)
                2 -> tvThird.setBackgroundResource(R.drawable.bg_text_checked)
                3 -> tvFourth.setBackgroundResource(R.drawable.bg_text_checked)
            }
        }
    }

    private fun setDefaultViews() {
        tvFirst.text = ""
        tvSecond.text = ""
        tvThird.text = ""
        tvFourth.text = ""
        tvFirst.setBackgroundResource(R.drawable.bg_text_normal)
        tvSecond.setBackgroundResource(R.drawable.bg_text_normal)
        tvThird.setBackgroundResource(R.drawable.bg_text_normal)
        tvFourth.setBackgroundResource(R.drawable.bg_text_normal)
    }

    fun getText(): String {
        return if (codes.size == 0) ""
        else {
            var result = ""
            for (code in codes) {
                result += code
            }
            result
        }
    }

    fun setListener(block: (Boolean) -> Unit) {
        this.block = block
    }
}