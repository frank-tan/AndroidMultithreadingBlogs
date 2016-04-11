package com.franktan.multithreadingblogs;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tan on 10/04/2016.
 */
public class Util {
    public static final String LOG_TAG = "BackgroundThread";

    public static String getReadableTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}
