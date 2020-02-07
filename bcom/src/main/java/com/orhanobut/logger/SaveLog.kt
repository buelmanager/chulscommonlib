package com.orhanobut.logger

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object SaveLog {

    private val LOG_PATH = "/.wjcommon2/log/aimanager/Logs/"
    private val MAX_LENGH_TEXT = 3000
    private var mTAG = "aimanager"

    /**
     * The s file log path.
     */
    private var sFileLogPath: String? = null

    private val LogFileNameBackup = "log_backup.txt"
    private val MAX_SIZE = 2 * 1024 * 1024

    enum class LogType {
        V, D, I, W, E, WTF
    }

    fun initialize(strTag: String) {
        mTAG = strTag
        setFileLogPath(Environment.getExternalStorageDirectory().toString() + LOG_PATH)
    }

    private fun getCallerFile(message: String): String {
        val e = Exception()
        if (e.stackTrace != null && e.stackTrace.size >= 2) {
            val el2 = e.stackTrace[2]
            val el3 = e.stackTrace[3]
            val el4 = e.stackTrace[4]

            return (message + "("
                    + el2.fileName + " " + el2.methodName + " " + el2.lineNumber + ", "
                    + el3.fileName + " " + el3.methodName + " " + el3.lineNumber + ", "
                    + el4.fileName + " " + el4.methodName + " " + el4.lineNumber + ", "
                    + ")")
        } else {
            return message
        }
    }

    private fun saveLogToFile(
        type: LogType,
        msg: String,
        state: Int
    ) {

        val head = type.toString()
        val message = "[$head] $msg"

        val status = Environment.getExternalStorageState()
        if (!status.equals(Environment.MEDIA_MOUNTED, ignoreCase = true)) {
            Log.e(mTAG, "SDCard Status:$status")
            return
        }

        val dirPath = sFileLogPath
        if (TextUtils.isEmpty(dirPath)) {
            return
        }

        val dir = File(dirPath!!)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(mTAG, "[saveLogToFile] Make Directory Error...")
                return
            }
        }

        try {
            val f = SimpleDateFormat(
                "MM/dd HH:mm:ss:SSS",
                Locale.getDefault()
            )
            val logTime = f.format(Date())
            val p = logTime + "\t" + message + "\n\r"

            val file = File(dirPath + "Log.txt")
            if (file.exists()) {
                if (file.length() > MAX_SIZE) {

                    val backupFile = File(dirPath, LogFileNameBackup)
                    if (backupFile.exists()) {
                        if (!backupFile.delete()) {
                            Log.e(mTAG, "[saveLogToFile] Backup file delete Error...")
                        }
                    }

                    backupFile(file, backupFile)
                }
            }

            val fos: FileOutputStream
            if (state == 1) {
                fos = FileOutputStream(
                    file,
                    false
                )
            } else {
                fos = FileOutputStream(
                    file,
                    true
                )
            }
            fos.write(p.toByteArray())
            fos.close()
        } catch (ex: Exception) {
            handleException(ex)
        }

    }

    private fun backupFile(
        file: File?,
        new_name: File
    ): Boolean {
        return (file != null
                && file.exists()
                && file.renameTo(new_name))
    }

    fun getStackTrace(throwable: Throwable?): String {
        if (throwable == null) {
            return ""
        }

        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))

        return sw.buffer.toString()
    }


    fun setFileLogPath(path: String) {
        sFileLogPath = path
    }

    fun save(type: LogType, log: String) {
        var addStackStrlog = getCallerFile(log)
        saveLogToFile(type, addStackStrlog, 0)
    }

    fun handleException(ex: Exception) {
        if (!TextUtils.isEmpty(ex.message))
            Log.e(mTAG, "[handleException message] : " + ex.message)

        Log.e(mTAG, getStackTrace(ex))
    }

}
