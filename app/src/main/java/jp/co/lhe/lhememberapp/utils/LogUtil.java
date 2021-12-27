package jp.co.lhe.lhememberapp.utils;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * ログ出力共通Utility
 */
public class LogUtil {

    private static final String LOG_TAG = "LHE_LOG";

    /**
     * デバッグログ出力
     * @param message 出力メッセージ
     */
    public static void logDebug(String message) {
        final StackTraceElement trace = Thread.currentThread().getStackTrace()[3];

        final String cla = trace.getClassName();
        Pattern pattern = Pattern.compile("[\\.]+");
        final String[] splitedStr = pattern.split(cla);
        final String simpleClass = splitedStr[splitedStr.length - 1];

        final String mthd = trace.getMethodName();
        final int line = trace.getLineNumber();

        String tag = LOG_TAG + " " + simpleClass;

        String methodLine = mthd + "():" + line + " ";
        Log.d(tag, methodLine + message);
    }

    /**
     * エラーログ出力
     * @param message 出力メッセージ
     */
    public static void logError(String message) {
        final StackTraceElement trace = Thread.currentThread().getStackTrace()[3];

        final String cla = trace.getClassName();
        Pattern pattern = Pattern.compile("[\\.]+");
        final String[] splitedStr = pattern.split(cla);
        final String simpleClass = splitedStr[splitedStr.length - 1];

        final String mthd = trace.getMethodName();
        final int line = trace.getLineNumber();

        String tag = LOG_TAG + " " + simpleClass;

        String methodLine = mthd + "():" + line + " ";
        Log.e(tag, methodLine + message);
    }
}
