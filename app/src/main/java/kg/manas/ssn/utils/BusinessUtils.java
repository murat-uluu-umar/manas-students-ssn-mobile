package kg.manas.ssn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

public class BusinessUtils {
    public static final String dateFormat = "HH:mm";

    public static Date getRandomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, getRandom(8, 16));
        gc.set(Calendar.MINUTE, getRandom(0, 59));
        return gc.getTime();
    }
    public static Date getDate(int hours,int minutes) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, hours);
        gc.set(Calendar.MINUTE, minutes);
        return gc.getTime();
    }
    @SuppressLint("SimpleDateFormat")
    public static String getFormattedHours(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }
    public static int getRandom(int min, int max){
        return new Random().nextInt(max + 1 - min) + min;
    }
    public static int getRandColor(){
        return Color.argb(255, getRandom(0, 255), getRandom(0, 255), getRandom(0, 255));
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    public static DateTime stringToDateTime(String format, String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format).withLocale(Locale.ENGLISH);
        return dateTimeFormatter.parseDateTime(date).toDateTime(DateTimeZone.UTC);
    }
    public static Point getDisplaySize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }
}
