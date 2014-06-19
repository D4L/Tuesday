package com.maxclique.tuesday.app;

import android.content.Context;

/**
 * Created by Austin on 6/18/2014.
 */
public class TimeAgoParser {

    static final long SEC = 1000;
    static final long MIN = 60 * SEC;
    static final long HOUR = 60 * MIN;
    static final long DAY = 24 * HOUR;

    static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    static String timeAgoSince(Context c, long time) {
        long milliDifference = getCurrentMillis() - time;
        if (milliDifference < 10 * SEC) {
            return c.getString(R.string.second_ago);
        } else if (milliDifference < MIN) {
            return (int)(milliDifference / SEC) + c.getString(R.string.seconds_ago);
        } else if (milliDifference < 2*MIN) {
            return c.getString(R.string.minute_ago);
        } else if (milliDifference < HOUR) {
            return (int)(milliDifference / MIN) + c.getString(R.string.minutes_ago);
        } else if (milliDifference < 2*HOUR) {
            return c.getString(R.string.hour_ago);
        } else if (milliDifference < DAY) {
            return (int)(milliDifference / HOUR) + c.getString(R.string.hours_ago);
        } else if (milliDifference < 2*DAY) {
            return c.getString(R.string.yesterday);
        } else {
            return c.getString(R.string.too_long_ago);
        }
    }
}
