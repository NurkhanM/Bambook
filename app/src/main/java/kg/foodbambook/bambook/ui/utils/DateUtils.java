package kg.foodbambook.bambook.ui.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
    private static final String TAG = DateUtils.class.getSimpleName();
    private static final TimeZone UTC;
    /**
     * 0001-01-01T00:00:00.000000Z
     * 2019-04-17T17:14:00.310714+06:00
     */
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String ISO_8601_24H_FULL_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ISO_8601_24H_FULL_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";

    static
    {
        UTC = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(UTC);
    }

    public static Date convertToDate(String time) throws ParseException {

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT, java.util.Locale.getDefault());
            sdf.setTimeZone(UTC);
            return sdf.parse(time);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
        return  null;
    }

    public static Date convertToDate2(String time) throws ParseException {

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT_2, java.util.Locale.getDefault());
            sdf.setTimeZone(UTC);
            return sdf.parse(time);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
        return  null;
    }

    public static Date convertToDate3(String time) throws ParseException {

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT_3, java.util.Locale.getDefault());
            sdf.setTimeZone(UTC);
            return sdf.parse(time);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
        return  null;
    }
}