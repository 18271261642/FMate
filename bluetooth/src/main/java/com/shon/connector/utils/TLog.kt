package com.shon.connector.utils

import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

     class TLog {


         companion object {
             val LOG_TAG = "HelpLog"
             var DEBUG = true
             private val LINE_SEPARATOR = System.getProperty("line.separator")
             private val JSON_INDENT = 4
             fun TLog() {}

             fun analytics(log: String) {
                 if (DEBUG)
                     Log.d(LOG_TAG, getClassInfo() + log)
             }

             fun error(log: String) {
                 if (DEBUG)
                     Log.e(LOG_TAG, getClassInfo() + log)
             }

             fun error(tagStr: String,log: String){
                 if (DEBUG)
                     Log.e(tagStr, getClassInfo() + log)
             }

             fun log(log: String) {
                 if (DEBUG)
                     Log.i(LOG_TAG, getClassInfo() + log)
             }

             fun log(tag: String, log: String) {
                 if (DEBUG)
                     Log.i(tag, getClassInfo() + log)
             }

             fun logv(log: String) {
                 if (DEBUG)
                     Log.v(LOG_TAG, getClassInfo() + log)
             }

             fun warn(log: String) {
                 if (DEBUG)
                     Log.w(LOG_TAG, getClassInfo() + log)
             }

             /***
              * @param tagStr
              * @param json
              */
             fun json(tagStr: String, json: String) {
                 val msg: String
                 if (!DEBUG) {
                     return
                 } else {
                     msg = json
                     val logStr = getClassInfo()

                     if (TextUtils.isEmpty(msg)) {
                         log(tagStr, "Empty or Null json content")
                         return
                     }

                     var message: String? = null

                     try {
                         if (msg.startsWith("{")) {
                             val jsonObject = JSONObject(msg)
                             message = jsonObject.toString(JSON_INDENT)
                         } else if (msg.startsWith("[")) {
                             val jsonArray = JSONArray(msg)
                             message = jsonArray.toString(JSON_INDENT)
                         }
                     } catch (e: JSONException) {
                         error(e.cause!!.message + "\n" + msg)
                         return
                     }

                     printLine(tagStr, true)
                     message = logStr + LINE_SEPARATOR + message
                     val lines = message.split(LINE_SEPARATOR!!.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                     val jsonContent = StringBuilder()
                     for (line in lines) {
                         jsonContent.append("║ ").append(line).append(LINE_SEPARATOR)
                     }
                     log(tagStr, jsonContent.toString())
                     printLine(tagStr, false)

                 }
             }

             /***
              *
              * @param tag
              * @param isTop
              */
             private fun printLine(tag: String, isTop: Boolean) {
                 if (isTop) {
                     log(
                         tag,
                         "╔═══════════════════════════════════════════════════════════════════════════════════════"
                     )
                 } else {
                     log(
                         tag,
                         "╚═══════════════════════════════════════════════════════════════════════════════════════"
                     )
                 }
             }

             /***
              * @return获取相关类、方法信息
              */
             private fun getClassInfo(): String {
                 val index = 4
                 val stackTrace = Thread.currentThread().stackTrace
                 val className = stackTrace[index].fileName
              //   var methodName = stackTrace[index].methodName
                 val lineNumber = stackTrace[index].lineNumber
           //      methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1)
                 val stringBuilder = StringBuilder()
                 stringBuilder.append("(").append(className).append(":").append(lineNumber).append(")").append("→")
                 return stringBuilder.toString()
             }
         }
}