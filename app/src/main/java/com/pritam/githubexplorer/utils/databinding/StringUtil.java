package com.pritam.githubexplorer.utils.databinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtil {

    public static String stringtoDateFormat(String info, String dates) {
        String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2019-07-14T06:56:42Z
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String dateStr = "";
        Date date;
        try {
            date = new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(dates);
            dateStr = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!dateStr.equals("") && info != null && info.length() > 0){
            info = info + " ";
        } else {
            info = "";
        }
        return info + dateStr;
    }

}
