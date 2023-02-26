package com.app.fmate.ui.fragment.home

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.CardDeleteAdapter
import com.app.fmate.adapter.CardEditAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.cardView.CardViewModel
import com.app.fmate.network.api.cardView.EditCardVoBean
import com.shon.connector.utils.ShowToast
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_card_edit.*

/**
 * 编辑卡片
 */
class CardEditActivity : BaseActivity<CardViewModel>() {
    lateinit var mCardEditAdapter: CardEditAdapter
    lateinit var mCardDeleteAdapter: CardDeleteAdapter
    private var mAddList: MutableList<EditCardVoBean.AddedListDTO> = arrayListOf()
    private var mDeleteList: MutableList<EditCardVoBean.MoreListDTO> = arrayListOf()
    private var  mHomeCardBean1:EditCardVoBean= EditCardVoBean()
    override fun layoutId() = R.layout.activity_card_edit
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        if (mHomeCardBean1.addedList != null)
            mAddList = mHomeCardBean1.addedList
        if (mHomeCardBean1.moreList != null)
            mDeleteList = mHomeCardBean1.moreList
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                updateCard()
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
                updateCard()
                finish()
            }

        })
        // 拖拽监听
        val listener: OnItemDragListener = object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                TLog.error("onItemDragStart+="+pos)
                val holder = viewHolder as BaseViewHolder

                    val startColor = Color.WHITE
                    val endColor = Color.rgb(245, 245, 245)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val v =
                            ValueAnimator.ofArgb(startColor, endColor)
                        v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                        v.duration = 300
                        v.start()
                }
            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder,
                from: Int,
                target: RecyclerView.ViewHolder,
                to: Int
            ) {

                TLog.error("onItemDragMoving  += from==${from}  to==${to}")
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                TLog.error("onItemDragEnd+="+pos)
                val holder = viewHolder as BaseViewHolder
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v =
                        ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                updateCardTest()
            }
        }
        mCardEditAdapter = CardEditAdapter(mAddList)
        mCardDeleteAdapter = CardDeleteAdapter(mDeleteList)
        mCardEditAdapter?.let {
            it.draggableModule?.isDragEnabled = true
            it.draggableModule?.setOnItemDragListener(listener)
            it.draggableModule?.toggleViewId=R.id.imgDrag //子控件
            it.draggableModule?.isDragOnLongPressEnabled=false
            it.draggableModule?.itemTouchHelperCallback?.setDragMoveFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN)

        }
        ryCardAdd.adapter = mCardEditAdapter
        ryCardMore.adapter = mCardDeleteAdapter
        mCardEditAdapter.addChildClickViewIds(R.id.imgDeleteAdd,R.id.imgDrag)
        mCardEditAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imgDeleteAdd->{
                if (mAddList.size <= 1) {
                    ShowToast.showToastLong("请至少保留一个卡片")
                    return@setOnItemChildClickListener
                }
                mDeleteList.add(
                    EditCardVoBean.MoreListDTO(
                        mCardEditAdapter.data[position].type,
                        position,
                        mCardEditAdapter.data[position].name,
                        !mCardEditAdapter.data[position].isHidden,
                    )
                )
                mAddList.removeAt(position)
                mCardEditAdapter.notifyItemRemoved(position)
                mCardDeleteAdapter.notifyItemChanged(position)
                loadingVisibility()
                }
                R.id.imgDrag->
                {

                }
            }
        }
        mCardDeleteAdapter.addChildClickViewIds(R.id.imgDeleteAdd)
        mCardDeleteAdapter.setOnItemChildClickListener { adapter, view, position ->
            mAddList.add(
                EditCardVoBean.AddedListDTO(
                    mCardDeleteAdapter.data[position].type,
                    position,
                    mCardDeleteAdapter.data[position].name,
                   !mCardDeleteAdapter.data[position].isHidden,
                )
            )
            mDeleteList.removeAt(position)
            mCardEditAdapter.notifyItemChanged(position)
            mCardDeleteAdapter.notifyItemRemoved(position)
            loadingVisibility()
        }
        loadingVisibility()
        mViewModel.getAllCard()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.resultGetCard.observe(this)
        {
            if(it==null||it.addedList==null||it.moreList==null)
                return@observe
                mDeleteList.addAll(it.moreList)
                mAddList.addAll(it.addedList)
            loadingVisibility()
            mCardEditAdapter.notifyDataSetChanged()
            mCardDeleteAdapter.notifyDataSetChanged()
        }
        mViewModel.resultUpdate.observe(this)
        {
            SNEventBus.sendEvent(Config.eventBus.HOME_CARD)
        }

    }
    private fun updateCard() {
        mHomeCardBean1.addedList = mAddList
        mHomeCardBean1.moreList = mDeleteList
     //   Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
        mViewModel.updateCard(Gson().toJson(mAddList),Gson().toJson(mDeleteList))
        SNEventBus.sendEvent(Config.eventBus.HOME_CARD)
    }
    private fun updateCardTest() {
        mHomeCardBean1.addedList = mAddList
        mHomeCardBean1.moreList = mDeleteList
        //   Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
    }

    private fun loadingVisibility() {
        if (mDeleteList.size <= 0 || mDeleteList == null) {
            tvMore.visibility = View.GONE
            cvMoreCard.visibility = View.GONE
            tvAdd.visibility = View.VISIBLE
            cvAddCard.visibility = View.VISIBLE
        } else if (mAddList.size <= 0 || mAddList == null) {
            tvAdd.visibility = View.GONE
            cvAddCard.visibility = View.GONE
            tvMore.visibility = View.VISIBLE
            cvMoreCard.visibility = View.VISIBLE
        } else {
            tvMore.visibility = View.VISIBLE
            cvMoreCard.visibility = View.VISIBLE
            tvAdd.visibility = View.VISIBLE
            cvAddCard.visibility = View.VISIBLE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            updateCard()
        }

        return super.onKeyDown(keyCode, event)
    }
}