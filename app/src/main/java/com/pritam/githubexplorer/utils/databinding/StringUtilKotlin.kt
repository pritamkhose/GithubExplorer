package com.pritam.githubexplorer.utils.databinding

import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StringUtilKotlin {

    companion object
    {
        @JvmStatic
        fun getQuantityString(quantity: Int): String {
            return "Qty: $quantity"
        }

        @JvmStatic
        fun convertIntToString(value: Int): String {
            return "($value)"
        }

        @JvmStatic
        fun stringtoDateFormat(info: String?, dates: String): String {
            var info = info
            val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'" //2019-07-14T06:56:42Z
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            var dateStr = ""
            val date: Date
            try {
                date = SimpleDateFormat(DATE_FORMAT_PATTERN).parse(dates)
                dateStr = sdf.format(date).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (dateStr !== "" && info != null && info.length > 0) {
                info = "$info "
            } else {
                info = ""
            }
            return info + dateStr
        }

    }

}