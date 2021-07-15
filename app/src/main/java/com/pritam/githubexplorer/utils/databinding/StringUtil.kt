package com.pritam.githubexplorer.utils.databinding

import java.text.SimpleDateFormat
import java.util.*

class StringUtil {

    companion object {

        @JvmStatic
        fun stringDateFormat(info: String, dates: String?) : String {

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

            var infoTemp = info
            infoTemp = if (dateStr !== "" && infoTemp.isNotEmpty()) {
                "$info "
            } else {
                ""
            }
            return infoTemp + dateStr
        }

    }

}