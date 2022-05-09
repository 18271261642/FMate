package com.example.xingliansdk
import android.os.Bundle
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.YearBean
import com.example.xingliansdk.bean.room.MotionListBean
import com.example.xingliansdk.bean.room.MotionListDao
import com.example.xingliansdk.network.api.UIUpdate.UIUpdateBean
import com.example.xingliansdk.utils.FileUtils
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.view.DownloadProgressButton
import com.google.gson.Gson
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.utils.HexDump
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class TestActivity : BaseActivity<TestViewModel>(), BleWrite.FlashWriteAssignInterface,
    BleWrite.FlashErasureAssignInterface, DownLoadCallback {

    lateinit var motionListDao: MotionListDao
    lateinit var bean: UIUpdateBean
    override fun layoutId() = R.layout.activity_test

    override fun initView(savedInstanceState: Bundle?) {
        tvOnclick.setOnClickListener { addData() }
        tvOnclick2.setOnClickListener { loadData() }
     //   mViewModel.findUpdate("8001", "1001")
      //  download.isEnablePause=false
        download.currentText = "安装"
//        download.maxProgress=100
//        download.minProgress=0
        download.setOnClickListener {
            TLog.error("点击事件")
            download.reset()
            download.progress=1f
        }

        download.onDownLoadClickListener = object :DownloadProgressButton.OnDownLoadClickListener{
            override fun clickDownload() {
                TLog.error("clickDownload===")
                download.progress++
            }

            override fun clickPause() {
                TLog.error("clickPause===")
            }

            override fun clickResume() {
                TLog.error("clickResume===")
            }

            override fun clickFinish() {
                TLog.error("clickFinish===")
            }
        }

    }

    override fun onResume() {
        super.onResume()
     //   motionListDao = instance.getMotionListDao()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            TLog.error("IT==" + Gson().toJson(it))
            bean = it
            startByte = HexDump.toByteArray(it.startPosition.toLong())
            endByte = HexDump.toByteArray(it.endPosition.toLong())

            BleWrite.writeFlashErasureAssignCall(it.startPosition, it.endPosition, this)
        }
        mViewModel.msg.observe(this) {
            ShowToast.showToastLong(it)
        }
    }

    var keyData = byteArrayOf()  //测试用的几千个byte[]
    private fun addData() {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, 0)

        motionListDao.insertAll(getMonthList(calendar) as MutableList<MotionListBean>)

        calendar.set(Calendar.MONTH, 4)

        motionListDao.insertAll(getMonthList(calendar) as MutableList<MotionListBean>)


    }

    private fun loadData() {
        val calendar = Calendar.getInstance()
        loadMonthList(calendar)
    }

    private fun loadMonthList(calendar: Calendar) {
        val monthKey = DateUtil.getDate(DateUtil.YYYY, calendar)
        val list: MutableList<MotionListBean> = motionListDao.getList(monthKey)
        TLog.error("loadMonthList count ${list.size}")
        val monthList: ArrayList<YearBean> = ArrayList()
        for (i in 1..12) {
            val yearBeam = YearBean(calendar.get(Calendar.YEAR), i, 0)
            monthList.add(yearBeam)
        }
        for (motionListBean in list) {

            val calendar =
                DateUtil.convertLongToCalendar((motionListBean.startTime + Config.TIME_START) * 1000)

            TLog.error("loadMonthList month  ${calendar.get(Calendar.DAY_OF_MONTH)}")
            var find = monthList.find { yearBean ->
                yearBean.year == calendar.get(Calendar.YEAR) && yearBean.month == calendar.get(
                    Calendar.MONTH
                ) + 1
            }
            if (find == null) {
                //找不到，说明 monthList 里面，没有这个月份，需要添加
                find = YearBean(
                    calendar.get(Calendar.YEAR), calendar.get(
                        Calendar.MONTH
                    ) + 1, motionListBean.totalSteps
                )  //先把当前的步数，放到  totalStep 里面
                monthList.add(find)
            } else {
                //如果存在，更新数据值
                TLog.error("进了 else ")
                find.totalStep = find.totalStep + motionListBean.totalSteps
                val indexOf = monthList.indexOf(find)
                monthList[indexOf]
                monthList[indexOf] = find //更新列表
            }
        }
        for (yearBean in monthList) {
            TLog.error("loadMonthList yearBean $yearBean")
        }


    }

    private fun getMonthList(calendar: Calendar): List<MotionListBean> {
        val list: ArrayList<MotionListBean> = ArrayList()
        val count = getMonthDayCount(calendar)
        TLog.error("getMonthList count $count")
        for (i in 1..count) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startTime = DateUtil.convertDateToLong(calendar.time)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endTime = DateUtil.convertDateToLong(calendar.time)

            val montionBean = MotionListBean(
                startTime / 1000 - Config.TIME_START,
                endTime / 1000 - Config.TIME_START,
                "stepStr",
                (Math.random() * 100).toLong(),
                false,
                DateUtil.getDate(DateUtil.YYYY_MM_DD, startTime)
            )
            TLog.error("getMonthList montionBean = $montionBean")
            TLog.error("==${Gson().toJson(motionListDao.getAllRoomMotionList())}")
            TLog.error(
                "==${
                    Gson().toJson(
                        motionListDao.getTimeStepList(
                            startTime / 1000 - Config.TIME_START,
                            endTime / 1000 - Config.TIME_START
                        )
                    )
                }"
            )
            TLog.error(
                "==${
                    Gson().toJson(
                        motionListDao.getRoomTime(
                            startTime / 1000 - Config.TIME_START,
                            endTime / 1000 - Config.TIME_START
                        )
                    )
                }"
            )
            //  list.add(motionListDao.getRoomTime(startTime / 1000 - Config.TIME_START,endTime / 1000 - Config.TIME_START))
        }
        return list
    }

    private fun getMonthDayCount(calendar: Calendar): Int {
        val year = calendar.get(Calendar.YEAR)
        return when (calendar.get(Calendar.MONTH) + 1) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> if (year % 4 == 0) {
                29
            } else {
                28
            }
            else -> {
                30
            }
        }
    }

    private var fileName: String? = null
    override fun onDownLoadStart(fileName: String?) {
        TLog.error("file++$fileName")
        this.fileName = fileName
    }

    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
        tvSend.text = "下载进度:${progress}%"
    }

    var length = 0
    override fun onDownLoadSuccess() {
        TLog.error("下载 成功")
        TLog.error("fileName++" + fileName)
        keyData = FileUtils.inputStream2ByteArray(fileName)
        TLog.error("===" + keyData)
        TLog.error("===" + keyData.size)
        length = keyData.size+ ceil(keyData.size.toDouble()/200).toInt()
        TLog.error("length++"+length)
        TLog.error("长度===" + FileUtils.inputStream2ByteArray(fileName).size)
        TLog.error("长度===" + ByteUtil.getHexString(FileUtils.inputStream2ByteArray(fileName)))
        sendFlash()
    }

    override fun onDownLoadError() {
        TLog.error("下载 失败")
    }

    val mList = java.util.ArrayList<ByteArray>() //组装的 list  现在装了所有数据暂时没做任何操作
    var startByte = ByteArray(4)  //开始位置
    var endByte = ByteArray(4)      //结束位置

    private fun sendFlash() {

        TLog.error("keyData++" + keyData.size)
        var CRC = HexDump.byteXOR(keyData) //整体的 crc由于byte过长所以要提前传输过去

        TLog.error("startByte+" + startByte.size)
        TLog.error("startByte+" + ByteUtil.getHexString(startByte))
        ///////
        var i = 0
        var size = 0
        var bt3 = ByteArray(200)
        while (i < keyData.size) {
            bt3 = ByteArray(200)
            if (keyData.size - (i + 200) >= 0) //超过200
            {
                for (j in 0 until 200) {
                    bt3[j] = keyData[i + j]
                }
                mList.add(bt3)
                TLog.error("最终bt3长度++" + bt3.size)
            } else {
                bt3 = ByteArray(keyData.size - i)
                for (j in 0 until (keyData.size - i)) { //没超过200就取相应长度 防止越界崩溃
                    bt3[j] = keyData[i + j]
                }
                mList.add(bt3)
                TLog.error("不足200 最终bt3长度++" + (keyData.size - (i + 200)))
                TLog.error("不足200 最终bt3长度++" + bt3.size)
            }
            i += 200

            size++
            TLog.error("==" + size)
            TLog.error("==" + i)
        }
        BleWrite.writeFlashWriteAssignCall(  //此处发永远是把所有数据整合起来发送mlist的第一条
            mList[0], startByte, endByte, 0, length, CRC, this
        )
    }

    override fun onResultFlash(size: Int, type: Int) {
        TLog.error("返回 size++" + size)
        if (mList.size > size)
            BleWrite.writeFlashWriteAssignCall(  //此处发永远是把所有数据整合起来发送mlist的第一条
                mList[size], size,mList.size, this
            )
    }

    override fun onResultErasure(key: Int) {
        if (key == 2) {
            if (bean != null && bean.ota.isNotEmpty()) {
                mViewModel.downLoadBin(bean, this)
            }
        } else
            ShowToast.showToastLong("不支持擦写FLASH数据")
    }
}
