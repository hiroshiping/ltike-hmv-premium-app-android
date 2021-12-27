package jp.co.lhe.lhememberapp.ui.utils;

import android.content.Context;

import java.util.ArrayList;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.network.models.LoginResult;

public class AccountUtils {

    /**
     * ログインできたかどうか
     * @param context
     * @param response
     * @return
     */
    public static boolean chkSuccessfullyLoggedIn(Context context, LoginResult.Response response) {
        boolean result;

        if (response==null || response.authResult ==null ||response.payStatus ==null || response.lcusNo ==null || response.contractCourse ==null ){
            result = false;
        } else {
            String authResult = response.authResult;
            String payStatus = response.payStatus;
            //ログインOKの条件：authResult＝1＆payStatus＝1
            if (authResult.equals("1") && payStatus.equals("1")) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * ログイン後の共通処理
     * @param context
     * @param response
     */
    public static void loginPostProcessing(Context context, LoginResult.Response response) {
        String payStatus = response.payStatus;
        String contractCourse = response.contractCourse;
        //有料区分、契約コースを保存
        ((LMApplication)context.getApplicationContext()).getLmaSharedPreference().setLoginAccountInfo(payStatus, contractCourse);
    }

    public static String[] getSavedQuestionPrefectures(Object s01) {
        String[] savedPrefectures = null;
        if (s01 != null) {
            // 選択済みの都道府県
            int s01Count = ((ArrayList<Double>) s01).size();
            savedPrefectures = new String[s01Count];
            for (int i = 0; i < s01Count; i++) {
                // doubleで管理されているので、小数点以下を削除
                savedPrefectures[i] = String.valueOf((int) (((ArrayList<Double>) s01).get(i) * 1));
            }
        }
        return savedPrefectures;
    }
    public static String[] getSavedQuestionServices(Object s02) {
        String[] savedServices = null;
        if (s02 != null) {
            // 選択済みのサービス
            int s02Count = ((ArrayList<Double>) s02).size();
            savedServices = new String[s02Count];
            for (int i = 0; i < s02Count; i++) {
                // doubleで管理されているので、小数点以下を削除
                savedServices[i] = String.valueOf((int) (((ArrayList<Double>) s02).get(i) * 1));
            }
        }

        return savedServices;
    }
}
