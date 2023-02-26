package com.app.fmate.ui.exerciseRecord

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.node.BaseNode
import com.app.fmate.R
import com.app.fmate.adapter.node.ExerciseRecordAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.node.DateRootNode
import com.app.fmate.bean.node.ItemExerciseRecordNode
import com.app.fmate.bean.room.AppDataBase
import com.app.fmate.bean.room.RoomExerciseRecordDao
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_exercise_reord.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseRecordActivity : BaseActivity<BaseViewModel>() {

    lateinit var mExerciseRecordAdapter: ExerciseRecordAdapter
    private lateinit var mExerciseDao: RoomExerciseRecordDao
    private lateinit var dataList:ArrayList<String>
    private lateinit var mExerciseList:ArrayList<ItemExerciseRecordNode>
    override fun layoutId() = R.layout.activity_exercise_reord
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        mExerciseRecordAdapter = ExerciseRecordAdapter()
        mExerciseDao=AppDataBase.instance.getItemExerciseRecordNode()
//        mExerciseDao.insert( ItemExerciseRecordNode(
//            "20", //距离
//            "30",
//            "40",
//            "0",
//            10010,
//            Config.exercise.WALK, //类型
//            System.currentTimeMillis() + 10000
//            ,"2021-05-18"
//        ))

        mExerciseList= mExerciseDao.getAllByDateDesc() as ArrayList<ItemExerciseRecordNode>
        TLog.error("运动数据++${Gson().toJson(mExerciseList)}")
          dataList=removeDuplicate(mExerciseDao.getAllByDateDesc() as ArrayList<ItemExerciseRecordNode>)
//            TLog.error("removeDuplicate+"+removeDuplicate(mExerciseDao.getAllRoomTimes() as ArrayList<ItemExerciseRecordNode>))
        ryIndex.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ryIndex.adapter = mExerciseRecordAdapter
        mExerciseRecordAdapter.setNewData(getEntity())
        mExerciseRecordAdapter.setEmptyView(R.layout.empty_view)
    }
    //模拟数据
    private fun getEntity(): MutableList<BaseNode>? {
        val list: MutableList<BaseNode> =
            ArrayList()
        for (i in 0 until dataList.size) {
            val items: MutableList<ItemExerciseRecordNode> =
                ArrayList()
            for (j in 0 until  mExerciseList.size) {
                if (mExerciseList[j].dateTime.indexOf(dataList[i])!=-1)
                {
                    items.add(mExerciseList[j])
                }
            }
            val beans: MutableList<ItemExerciseRecordNode> =
                ArrayList()
            for (i in items.indices) {
                val bean: ItemExerciseRecordNode = items[i]
                //先判断是否已经存在此type 存在的话就不需要第二次循环了
                var isContain = false
                for (j in beans.indices) {
                    if (beans[j].type == bean.type) {
                        isContain = true
                    }
                }
                if (!isContain) {
                    var total =0.0 //bean.distance.toDouble()
                    //重复type的累加 如果后面加过
                    for (j in i until items.size) {
                        if (items[j].type == bean.type) {
                            total += items[j].distance.toDouble()
                        }
                    }
                    beans.add(ItemExerciseRecordNode(items[i].type, total.toString()))
                }
            }
            val entity = DateRootNode(System.currentTimeMillis() + i * 10000000,
                items as List<BaseNode>?,beans as List<BaseNode>?,dataList[i]
            )
            list.add(entity)
        }
        return list
    }

    /**
     * 去重复生成一个 不重复的 datetime数组
     */
    private fun removeDuplicate(list: ArrayList<ItemExerciseRecordNode>): ArrayList<String> {
        val set: MutableSet<String> = LinkedHashSet()
        val mList:ArrayList<String> = arrayListOf()
        list.forEach {
            set.add(it.dateTime.substring(0,7))//只要年与月就好了
        }
        mList.addAll(set)
//        TLog.error("mList++"+Gson().toJson(mList))
        return mList
    }
    private fun getEmptyDataView(): View? {
        val notDataView: View =
            layoutInflater.inflate(R.layout.empty_view, ryIndex, false)
    //    notDataView.setOnClickListener { onRefresh() }
        return notDataView
    }
}
