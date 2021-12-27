package jp.co.lhe.lhememberapp.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import jp.co.lhe.lhememberapp.LMApplication;

/**
 * Created by lhedev on 2018/03/06.
 */

public class ScreenUtil {
    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = LMApplication.getApplication().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = LMApplication.getApplication().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * dp2px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp2px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px2dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2dp(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / scale + 0.5f);
    }

    /**
     * px2sp
     */
    public static int px2sp(Context context, float pxVal) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxVal / fontScale + 0.5);
    }

}
