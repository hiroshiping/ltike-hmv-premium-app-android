package jp.co.lhe.lhememberapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import jp.co.lhe.lhememberapp.LMApplication;

public class FirebaseUtil {

    private static final String TAG = FirebaseUtil.class.getSimpleName();

    private final static int VALUE_LENGTH = 98;

    /**
     * Firebase認証
     */
    public static void signInFirebaseAuth(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser == null){
                // 匿名認証、アカウントが必要ない
                mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                            }
                        }
                    });
            }
        }
    }

    public static void logEventLayoutCommunication(Context context, String className, String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        content = "JSON格納サーバー通信不可: "+ content;
        sendExceptionFile(context,1,"Layout_JSONエラー", className, content, nextAction);

        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "JSON格納サーバー通信不可");
        FirebaseAnalytics.getInstance(context).logEvent("Layout_JSON格納サーバー通信不可", bundle);
    }

    public static void logEventLayoutCommunication(Context context, String className, Throwable throwable, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String content = "JSON格納サーバー通信不可: "+ throwable.toString();
        sendExceptionFile(context,1,"Layout_JSONエラー", className, content, nextAction);
        content = throwable.getMessage();
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("Layout_JSON格納サーバー通信不可", bundle);
    }

    public static void logEventLayoutData(Context context, String className, Throwable throwable, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String content = "JSONデータエラー: "+ throwable.toString();
        sendExceptionFile(context,2,"Layout_JSONエラー", className, content, nextAction);
        content = throwable.getMessage();
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("Layout_JSONデータエラー", bundle);
    }

    public static void logEventLoginCommunication(Context context, String className, String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        content = "LWEBサーバー通信不可: "+ content;
        sendExceptionFile(context,3,"ログインエラー", className, content, nextAction);

        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "LWEBログイン通信不可");
        FirebaseAnalytics.getInstance(context).logEvent("ログイン_LWEBサーバー通信不可", bundle);
    }

    public static void logEventLoginCommunication(Context context, String className, Throwable throwable, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String content = "LWEBサーバー通信不可: "+ throwable.toString();
        sendExceptionFile(context,3,"ログインエラー", className, content, nextAction);
        content = throwable.getMessage();
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }

        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("ログイン_LWEBサーバー通信不可", bundle);
    }

    public static void logEventLoginData(Context context, String className, Throwable throwable, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String content = "JSONデータエラー: "+ throwable.toString();
        sendExceptionFile(context,4,"ログインエラー", className, content, nextAction);
        content = throwable.getMessage();
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("ログイン_JSONデータエラー", bundle);
    }

    public static void logEventLoginResult(Context context, String className, String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        sendExceptionFile(context,5,"ログインエラー", className, content, nextAction);
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("ログイン_認証失敗", bundle);
    }

    public static void logEventFunnelPushData(Context context, String className,  String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String message = "ユーザー情報が取得できません:"+ content;
        sendExceptionFile(context,6,"FunnelPushエラー", className, message, nextAction);
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("FunnelPush_ユーザー情報取得失敗", bundle);
    }

    public static void logEventFunnelPushOption(Context context, String className,  String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        String message = "サーバー通信不可:"+ content;
        sendExceptionFile(context,7,"FunnelPushエラー", className, message, nextAction);
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("FunnelPush_サーバー通信不可", bundle);
    }

    public static void logEventunKnown(Context context, String className, String content, String nextAction){
        // FireStore安全ルール使用の為、認証が必要です
        signInFirebaseAuth();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Login");

        sendExceptionFile(context,8,"その他エラー", className, "想定外のエラーが発生しました:"+ content, nextAction);
        if(content.length() > VALUE_LENGTH){
            content = content.substring(0, VALUE_LENGTH);
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, content);
        FirebaseAnalytics.getInstance(context).logEvent("その他エラー", bundle);
    }

    public static String getActionDialog(String title, String message){
        return "ポップアップ表示する:"+ title;
    }

    public static String getActionMessage(String content){
        return content;
    }

    private static void sendExceptionFile(Context context, int id, String name, String className, String content, String nextAction){
        FirebaseFirestore ds = FirebaseFirestore.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        Date date = getJapanZoneTime();
        String collectionName = sdf.format(date);
        String deviceSN = getDeviceSerialNumber();
        String customer = getCustomerId(context);
        String documentName = null;

        Map<String, Object> user = new HashMap<>();
        user.put("エラータイプID", id);
        user.put("ディバイスの識別ID", deviceSN);
        user.put("lwebの顧客ID", customer);
        user.put("ディバイスモデル", Build.MODEL+ " "+ Build.DEVICE);
        user.put("OSバージョン", Build.VERSION.RELEASE);
        user.put("エラー情報", content);
        user.put("依頼クラス", className);
        sdf = new SimpleDateFormat("HH:mm:ss");
        user.put("発生時刻", sdf.format(date));
        user.put("エラータイプ", name);
        user.put("次のアくション", nextAction);
        sdf = new SimpleDateFormat("MM月/dd日/HH:mm:ss");
        documentName = sdf.format(date)+ "-"+ deviceSN;
        // Add a new document with a generated ID
        ds.collection(collectionName)
                .document(documentName)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document", e);
                    }
                });
    }

    private static String getCustomerId(Context context){
        String customerID = "unknown";
        String lcusNo = ((LMApplication) context.getApplicationContext()).getLcusNo();
        if(!StringUtils.isNullOrEmpty(lcusNo)){
            return lcusNo;
        }
        String account = ((LMApplication) context.getApplicationContext()).getAccountId();
        if(!StringUtils.isNullOrEmpty(account)){
            return account;
        }
        return customerID;
    }

    private static String getDeviceSerialNumber(){
        String id = FirebaseInstanceId.getInstance().getId();
        if(id!=null && !id.equals("")) {
            return id;
        }else {
            return "unKnown-" + (System.currentTimeMillis()%100000);
        }
    }

    private static Date getJapanZoneTime(){
        TimeZone oldZone = TimeZone.getDefault();
        TimeZone newZone = TimeZone.getTimeZone("GMT+9:00");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        Date date = c.getTime();
        if(date != null){
            int timeOffset = oldZone.getOffset(date.getTime()) - newZone.getOffset(date.getTime());
            System.out.println("time offset="+ timeOffset);
            return new Date(date.getTime() - timeOffset);
        }

        return date;
    }

}
