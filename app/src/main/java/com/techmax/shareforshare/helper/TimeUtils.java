package com.techmax.shareforshare.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ABC on 9/2/2017.
 */

public class TimeUtils {

    public TimeUtils() {
    }

    public CharSequence createDate(long timestamp,String format) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }
}
