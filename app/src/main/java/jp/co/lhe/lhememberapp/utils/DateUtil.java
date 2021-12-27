package jp.co.lhe.lhememberapp.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jp.co.lhe.lhememberapp.LMApplication;

public class DateUtil {
    private static final String LOG_TAG = "LHE_LOG.DATE_UTIL";

    /**
     * 指定した時間より経過しているかどうか
     * @param timeOutMinutes タイムアウト時間（ミリ秒）
     * @return true:経過している /false:経過していない
     */
    public static boolean isTimeOver(long timeOutMinutes, long lastOperationTime) throws ParseException {
        boolean result = false;

        //現在日時
        Calendar cal = Calendar.getInstance();
        long dateTimeNow = cal.getTime().getTime();

        long diff = TimeUnit.MILLISECONDS.toSeconds(dateTimeNow - lastOperationTime);

        if (diff > TimeUnit.MILLISECONDS.toSeconds(timeOutMinutes)) {
            result = true;
        }

        return result;
    }

    /**
     * 最終操作日時をプリファレンスに保存します。
     * @param context
     */
    public static void setLastOperationTime(Context context) {
        //現在日時
        Calendar cal = Calendar.getInstance();
        long dateTimeNow = cal.getTime().getTime();
        ((LMApplication)context.getApplicationContext()).getLmaSharedPreference().setLastOperationTime(dateTimeNow);
    }

    /**
     * 最終WebView操作日時をプリファレンスに保存します。
     * @param context
     */
    public static void setLastOperationOnWebViewTime(Context context) {
        //現在日時
        Calendar cal = Calendar.getInstance();
        long dateTimeNow = cal.getTime().getTime();
        ((LMApplication)context.getApplicationContext()).getLmaSharedPreference().setLastOperationOnWebViewTime(dateTimeNow);
    }

    /**
     * お知らせの配信日を表示用の形式に変換します。
     * @param epochMillTime
     */
    public static String convertSendDate(long epochMillTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochMillTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN);
        return sdf.format(calendar.getTime());
    }

}
