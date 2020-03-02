package com.buel.bcom

import android.content.Context
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.TimePicker
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.buel.bcom.interfaces.OnCompleteListener
import java.util.*

/**
 * Created by 19001283 on 2018-07-09.
 */
interface OnDialogSelectListner {
    fun onSelect(s: String)
}

object MaterialDailogUtil {

    var setDate: String = ""

    fun datePickerDialog(context: Context, icon: Int, selectListner: OnCompleteListener<String>?) {

        setDate = ""
        val pickerView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_datepicker, null)

        val datePicker = pickerView.findViewById<DatePicker>(R.id.datePicker)

        var dateDialog = MaterialDialog.Builder(context)

            .title(R.string.select_date)
            .customView(datePicker, false)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .cancelable(false)
            .onPositive { materialDialog: MaterialDialog, dialogAction: DialogAction ->

                val year = datePicker.year
                val mon = datePicker.month+1
                val day = datePicker.dayOfMonth

                setDate = "$year.$mon.$day"
                val calendar = Calendar.getInstance()
                calendar.set(year, mon, day)

                //val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                //selectListner?.onSuccess("days : $dayOfWeek")
                materialDialog.dismiss()

                timePickerDialog(context, icon, selectListner)
            }

            .show()
    }

    fun timePickerDialog(context: Context, icon: Int, selectListner: OnCompleteListener<String>?) {
        val pickerView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_timepicker, null)

        val timePicker: TimePicker = pickerView.findViewById<TimePicker>(R.id.timePicker)


        val dialog = MaterialDialog.Builder(context)
            .title("시간을 선택하세요.")
            .customView(timePicker, false)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)

            .cancelable(false)
            .onPositive { dialog, which ->
                val hour = timePicker.hour
                val minute = timePicker.minute
                val baseline = timePicker.baseline

                setDate += "T$hour:$minute"
                selectListner?.onSuccess(setDate)
                dialog.dismiss()
            }
            .show()

    }
}
