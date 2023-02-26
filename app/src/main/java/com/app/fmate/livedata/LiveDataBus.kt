package com.app.fmate.livedata

import com.kunminx.architecture.ui.callback.UnPeekLiveData


/**
 * LiveData 事件总线
 *
 */
object LiveDataBus {

    private val liveDataMap: HashMap<String, UnPeekLiveData<Any>> = HashMap()




    fun weatherInfo(): UnPeekLiveData<Int> {
        return "weatherInfo".createLiveData()
    }


    fun addressMessage():UnPeekLiveData<String>{
        return  "address".createLiveData()
    }

    fun switchRoleMessage():UnPeekLiveData<Boolean>{
        return "switchRole".createLiveData()
    }

    fun reloadMessage():UnPeekLiveData<Boolean>{
        return "reloadMessage".createLiveData()
    }


    /**
     * 创建 liveData ,参考 [createUnReadMessage]
     */
    private inline fun <reified T> String.createLiveData(allowNull: Boolean = true): UnPeekLiveData<T> {
        return T::class.java.createLiveData(this, allowNull)
    }


    private fun <T> Class<T>.createLiveData(key: String, allowNull: Boolean = true): UnPeekLiveData<T> {
        if (liveDataMap.containsKey(key)) {
            return liveDataMap[key] as UnPeekLiveData<T>
        }
        val liveData = UnPeekLiveData.Builder<T>().setAllowNullValue(allowNull).create();

        liveDataMap[key] = liveData as UnPeekLiveData<Any>
        return liveData
    }


}