package com.example

class ListTest {


    val alllist :MutableList<ByteArray> = mutableListOf()

    fun splitCount(count:ByteArray){
       var arraySize:Int = count.size/4096;

        val list :MutableList<ByteArray> = mutableListOf()
        if (count.size%4096>0){
            arraySize += 1
        }
        for (i in 0 until arraySize){
            val srcStart = i* 4096
            val array = ByteArray(4096)
            System.arraycopy(count,srcStart,array,0,array.size)
            list.add(array);
        }

        list.forEach {childrArry->

            val arraySize2:Int = childrArry.size/200;


            if (count.size%200>0){
                arraySize += 1
            }
            for (i in 0 until arraySize2){
                val srcStart = i* 200
                val array = ByteArray(200)
                System.arraycopy(childrArry,srcStart,array,0,array.size)
                alllist.add(array);
            }
        }
    }

}