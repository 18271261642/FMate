package cn.idic.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File

/**
 * @packageName: medicalmall
 * @fileName: FileProvider
 * @author: chitian
 * @date: 2019-07-11 15:13
 * @description:
 *
 */
object FileProvider {
    fun getUriForFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= 24) {
            getUriForFile24(context, file)
        } else {
            Uri.fromFile(file)
        }
    }

    fun getUriForPath(context: Context, file: String): Uri? {
        if (TextUtils.isEmpty(file)) {
            return null
        }
        val file1 = File(file)
        var fileUri: Uri?
        fileUri = if (Build.VERSION.SDK_INT >= 24) {
            getUriForFile24(context, file1)
        } else {
            Uri.fromFile(file1)
        }
        return fileUri
    }


    fun getUriForFile24(context: Context, file: File): Uri {
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            context.packageName + ".FileProvider",
            file
        )
    }


    fun setIntentDataAndType(
        context: Context,
        intent: Intent,
        type: String,
        file: File,
        writeAble: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(
                getUriForFile(
                    context,
                    file
                ), type
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
    }

    fun setIntentDataAndType(
        context: Context,
        intent: Intent,
        type: String,
        file: String,
        writeAble: Boolean
    ): Intent {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(
                getUriForPath(
                    context,
                    file
                ), type
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.parse(file), type)
        }
        return intent
    }

    fun setIntentAndType(
        context: Context,
        intent: Intent,
        type: String,
        file: String,
        writeAble: Boolean
    ): Intent {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(
                getUriForPath(
                    context,
                    file
                ), type
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                getUriForPath(context, file)
            )
        } else {
            intent.setDataAndType(Uri.fromFile(File(file)), type)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(file)))
        }
        return intent
    }

    fun setIntentype(
        context: Context,
        intent: Intent,
        type: String,
        file: String,
        writeAble: Boolean
    ): Intent {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(
                getUriForPath(
                    context,
                    file
                ), type
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                getUriForPath(context, file)
            )
        } else {
            intent.setDataAndType(Uri.fromFile(File(file)), type)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(file)))
        }
        intent.type = type
        return intent
    }

    fun setIntentData(context: Context, intent: Intent, file: File, writeAble: Boolean) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.data =
                getUriForFile(context, file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.data = Uri.fromFile(file)
        }
    }

    fun setIntentData(context: Context, intent: Intent, file: String, writeAble: Boolean) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.data =
                getUriForFile(context, File(file))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.data = Uri.parse(file)
        }
    }

    fun grantPermissions(context: Context, intent: Intent, uri: Uri, writeAble: Boolean) {

        var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (writeAble) {
            flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        intent.addFlags(flag)
        val resInfoList = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, flag)
        }
    }

}