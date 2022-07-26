package com.example.phoneareacodelibrary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.quicksidebar.QuickSideBarTipsView
import com.bigkoo.quicksidebar.QuickSideBarView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import java.util.*

/**
 * 创建：yiang
 *
 *
 * 描述：
 */
class PhoneAreaCodeActivity : AppCompatActivity(), OnQuickSideBarTouchListener {
    private var quickSideBarView: QuickSideBarView? = null
    private var quickSideBarTipsView: QuickSideBarTipsView? = null
    private var isEnglish = true
    private val sections: MutableList<String> = ArrayList()
    private var layoutManager: LinearLayoutManager? = null
    private var datalist: List<AreaCodeModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_area_code)

        isEnglish = true;

        quickSideBarView = findViewById<View>(R.id.quickSideBarView) as QuickSideBarView
        quickSideBarTipsView = findViewById<View>(R.id.quickSideBarTipsView) as QuickSideBarTipsView
        quickSideBarView!!.setOnQuickSideBarTouchListener(this)
        val titleColor = intent.getStringExtra("titleColor")
        val stickHeaderColor = intent.getStringExtra("stickHeaderColor")
        val title = intent.getStringExtra("title")
        val titleTextColor = intent.getStringExtra("titleTextColor")


        //界面标题
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        if (!TextUtils.isEmpty(title)) tvTitle.text = title
        if (!TextUtils.isEmpty(titleTextColor)) tvTitle.setTextColor(Color.parseColor(titleTextColor))


        //标题背景颜色
        if (!TextUtils.isEmpty(titleColor)) findViewById<View>(R.id.llTitle).setBackgroundColor(
            Color.parseColor(titleColor)
        )
        findViewById<View>(R.id.ivBack).setOnClickListener { onBackPressed() }

        //读取数据
        val json = Utils.readAssetsTxt(this, "phoneAreaCode")
        datalist = Utils.jsonToList(json)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        sortList(datalist)
        val adapter = PhoneAreaCodeAdapter()
        adapter.setDataList(datalist)
        if (!TextUtils.isEmpty(stickHeaderColor)) adapter.setStickHeaderColor(stickHeaderColor)
        recyclerView.adapter = adapter
        //设置recyclerView需要的Decoration;
        val decoration = StickyRecyclerHeadersDecoration(adapter)
        recyclerView.addItemDecoration(decoration)
        adapter.setOnItemClickListener { model ->
            val intent = Intent()
            intent.putExtra(DATAKEY, model)
            setResult(resultCode, intent)
            finish()
        }
        val tvLan = findViewById<TextView>(R.id.tvLan)
        if (!TextUtils.isEmpty(titleTextColor)) tvLan.setTextColor(Color.parseColor(titleTextColor))
        tvLan.setOnClickListener {
            isEnglish = !isEnglish
            tvLan.text = if (isEnglish) "中文" else "English"
            sortList(datalist)
            adapter.setDataList(datalist)
            adapter.setEnglish(isEnglish)
        }
    }

    /**
     * 根据国家中文拼音首字母排序
     *
     * @param datalist
     */
    private fun sortList(datalist: List<AreaCodeModel>?) {
        Collections.sort(
            datalist
        ) { o1, o2 ->
            if (isEnglish) {
                Utils.getFirstPinYin(o1.en)
                    .compareTo(Utils.getFirstPinYin(o2.en))
            } else Utils.getFirstPinYin(o1.name)
                .compareTo(Utils.getFirstPinYin(o2.name))
        }
        sections.clear()
        for (area in datalist!!) {
            var section = ""
            section = if (isEnglish) {
                Utils.getFirstPinYin(area.en)
            } else {
                Utils.getFirstPinYin(area.name)
            }
            if (!sections.contains(section)) sections.add(section)
        }
        quickSideBarView!!.letters = sections
    }

    override fun onLetterChanged(letter: String, position: Int, y: Float) {
        quickSideBarTipsView!!.setText(letter, position, y)
        layoutManager!!.scrollToPositionWithOffset(index(letter), 0)
    }

    private fun index(letter: String): Int {
        for (i in datalist!!.indices) {
            val area = datalist!![i]
            var section: String? = ""
            section = if (isEnglish) {
                Utils.getFirstPinYin(area.en)
            } else {
                Utils.getFirstPinYin(area.name)
            }
            if (TextUtils.equals(letter, section)) {
                return i
            }
        }
        return 0
    }

    override fun onLetterTouching(touching: Boolean) {
        quickSideBarTipsView!!.visibility = if (touching) View.VISIBLE else View.GONE
    }

    companion object {
        const val resultCode = 0x1110
        const val DATAKEY = "AreaCodeModel"
        @JvmStatic
        fun newInstance(
            context: Context?,
            title: String?,
            titleTextColor: String?,
            titleColor: String?,
            stickHeaderColor: String?
        ): Intent {
            val intent = Intent(context, PhoneAreaCodeActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("titleColor", titleColor)
            intent.putExtra("titleTextColor", titleTextColor)
            intent.putExtra("stickHeaderColor", stickHeaderColor)
            return intent
        }
    }
}