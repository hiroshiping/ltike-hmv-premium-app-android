package jp.co.lhe.lhememberapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.io.EOFException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.BaseObserver;
import jp.co.lhe.lhememberapp.network.RetrofitUtil;
import jp.co.lhe.lhememberapp.network.models.LatestAppUpdateModel;
import jp.co.lhe.lhememberapp.network.models.LayoutSettingModel;
import jp.co.lhe.lhememberapp.network.models.LoginResult;
import jp.co.lhe.lhememberapp.network.models.ToastInfo;
import jp.co.lhe.lhememberapp.ui.utils.AccountUtils;
import jp.co.lhe.lhememberapp.ui.views.PopUpAlertDialog;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.co.lhe.lhememberapp.utils.LogUtil;
import jp.co.lhe.lhememberapp.utils.RxUtil;
import jp.co.lhe.lhememberapp.utils.SharedPrefsUtils;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnMessagesDetailApiListener;
import jp.funnelpush.sdk.callback.OnUniqueCodeApiListener;
import jp.funnelpush.sdk.callback.OnUserIdChangedListener;
import jp.funnelpush.sdk.response.MessagesListResponse;
import jp.funnelpush.sdk.response.MessagesResponse;
import jp.funnelpush.sdk.response.UsersResponse;

import static jp.funnelpush.sdk.constant.a.s;

/**
 * Activity
 * 01-01.起動_スプラッシュ画面
 */
public class SplashActivity extends Activity {

    private Context mContext;
    private String mPushParamUrl = "";
    private String mPushParamMessageId = "";
    private CompositeDisposable mCompositeDisposable;

    private static final int MAX_TRY_COUNT = 2;
    private int mTryHandleCount = 0;
    private boolean mIsHandlingTimeOut = false;
    private SplashHandler mSplashHandler;

    @Override
    protected void onResume() {
        super.onResume();

        mContext = SplashActivity.this;
        // [android]端末画面チェック
        // 端末の画面サイズが320dp以下の端末は起動させない
        if (!chkDeviceDisplaySize()) {
            LogUtil.logError("ERROR chkDeviceDisplaySize ");
            new AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("画面の解像度が低いため、本端末ではご利用になれません。")
                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPushParamUrl = bundle.getString(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);
            mPushParamMessageId = bundle.getString(FunnelPush.FUNNEL_PUSH_MESSAGE_ID);
        }
        removeHandlerMessageIfNeed();
        mTryHandleCount = 0;
        // レイアウトJson取得
        getLayoutJson();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.logDebug("start");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mCompositeDisposable = new CompositeDisposable();
        mSplashHandler = new SplashHandler(this);
    }

    /**
     * [android]端末画面チェック
     * 端末の画面サイズが320dp以下かチェックする
     *
     * @return true:OK/false:NG
     */
    private boolean chkDeviceDisplaySize() {
        LogUtil.logDebug("start:" + mPushParamUrl);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;

        LogUtil.logDebug("chkDeviceDisplaySize width:" + dpWidth);

        if (320 > dpWidth) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * レイアウトJSON取得
     * 対象のサーバに配置されているレイアウトJSONをHTTP通信で取得する。
     */
    private void getLayoutJson() {
        LogUtil.logDebug("start:" + mPushParamUrl);

        // 実行
        RetrofitUtil.getInstance()
                .getAPI()
                .layoutJson(getString(R.string.layout_json_url))
                .compose(RxUtil.<LayoutSettingModel>observableSchedulerHelper())
                .subscribe(new BaseObserver<LayoutSettingModel>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(LayoutSettingModel layoutSettingModel) {
                        doCallbackGetLayoutJson(layoutSettingModel);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.d("onNetError");
                        throwable.printStackTrace();
                        //Firebase イベントをレポートする
                        String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                                ToastInfo.getNetErrorInfo().getMessage());
                        if(throwable instanceof JsonParseException || throwable instanceof MalformedJsonException ||
                            throwable instanceof JSONException || throwable instanceof JsonParseException) {
                            FirebaseUtil.logEventLayoutData(mContext, getLocalClassName(), throwable, nextAction);
                        }else{
                            FirebaseUtil.logEventLayoutCommunication(mContext, getLocalClassName(), throwable, nextAction);
                        }
                        showErrorToast(ToastInfo.getNetErrorInfo());
                    }
                });
    }

    /**
     * エラー内容をToastで表示します。
     *
     * @param toastInfo
     */
    private void showErrorToast(ToastInfo toastInfo) {
        new AlertDialog.Builder(this)
                .setTitle(toastInfo.getTitle())
                .setMessage(toastInfo.getMessage())
                .setCancelable(false)
                .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getLayoutJson();
                    }
                })
                .show();
        return;
    }

    /**
     * メンテナンスモード判定
     *
     * @return
     */
    private boolean chkIsMaintenanceMode() {
        return ((LMApplication) getApplicationContext()).getLmaSharedPreference().getIsNowMaintenance();
    }

    /**
     * レイアウトJson取得後の処理
     *
     * @param response
     */
    private void doCallbackGetLayoutJson(LayoutSettingModel response) {
        LogUtil.logDebug("start:" + mPushParamUrl);
        // レイアウトJSONの情報を保存
        saveLayoutJson(response);

        //メンテナンスモード判定
        if (chkIsMaintenanceMode()) {
            new AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("現在メンテナンス中です。しばらくたってから、もう一度やり直してください。")
                    .setCancelable(false)
                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
            return;
        }

        // アプリバージョンチェック
        if (!chkAppVersion()) {
            final LatestAppUpdateModel latestAppUpdate = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getlatestAppUpdateModel();
            new AlertDialog.Builder(this, R.style.VersionUpAlertStyle)
                    .setTitle(latestAppUpdate.updateAndroid.title)
                    .setMessage(latestAppUpdate.updateAndroid.message)
                    .setCancelable(false)
                    .setPositiveButton(latestAppUpdate.updateAndroid.button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(latestAppUpdate.updateAndroid.href);
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
            return;
        }

        // 画面遷移
        doScreenTransition();

    }

    /**
     * レイアウトJSONの情報を保存
     * 取得したレイアウトJSON内の情報をアプリに保持する
     */
    private void saveLayoutJson(LayoutSettingModel response) {
        ((LMApplication) getApplicationContext()).getLmaSharedPreference().setLayoutJsonData(response);
    }

    /**
     * アプリバージョンチェック
     * レイアウトJsonのアプリバージョンと比較して、レイアウトJsonのアプリバージョン以上でない場合はエラー
     */
    private boolean chkAppVersion() {
        LogUtil.logDebug("start:" + mPushParamUrl);

        boolean result = false;

        // アプリのバージョン取得
        PackageManager pm = getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //x.x.x形式
        String latestAppVersion = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getLatestAppVersion();

        String[] latestVersion = latestAppVersion.split("\\.");
        String[] currentVersion = versionName.split("\\.");

        if (latestVersion[0].equals(currentVersion[0]) && latestVersion[1].equals(currentVersion[1]) && latestVersion[2].equals(currentVersion[2])) {
            //バージョンと一致している
            result = true;
        } else {
            BigDecimal latestVersionFirstDecimal = new BigDecimal(latestVersion[0] + "." + latestVersion[1]);
            BigDecimal currentVersionFirstDecimal = new BigDecimal(currentVersion[0] + "." + currentVersion[1]);

            if (latestVersionFirstDecimal.equals(currentVersionFirstDecimal)) {
                if (Integer.parseInt(latestVersion[2]) > Integer.parseInt(currentVersion[2])) {
                    //バージョンが古い
                    result = false;
                } else {
                    //バージョンが新しい
                    result = true;
                }
            } else {
                if (currentVersionFirstDecimal.compareTo(latestVersionFirstDecimal) >= 0) {
                    //バージョンが新しい
                    result = true;
                } else {
                    //バージョンが古い
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * 画面遷移
     * 未ログイン時→ログイン画面へ
     */
    private void doScreenTransition() {
        LogUtil.logDebug("start:" + mPushParamUrl);
        // ログイン実行フラグ
        SharedPrefsUtils.setBooleanPreference(this, PopUpAlertDialog.POPUP_LOGIN_START, true);

        String lcusNo = ((LMApplication) getApplicationContext()).getLcusNo();
        if (lcusNo != null && !lcusNo.isEmpty()) {
            mIsHandlingTimeOut = false;
            mSplashHandler.sendEmptyMessageDelayed(SplashHandler.SET_UNIQUE_CODE_TIME_OUT,
                    SplashHandler.SET_UNIQUE_CODE_TIME_OUT_DELAY);

            // Actual Binding
            Log.d(getClass().getSimpleName(), "doScreenTransition: start to send unique code");
            FunnelPush.sendUniqueCode(getApplicationContext(), lcusNo,
                    FunnelPush.UniqueCodeType.FP_ORIGINAL, new OnUserIdChangedListener() {
                        @Override
                        public void onUserIdChanged(String s) {
                            Log.d(getClass().getSimpleName(), "[Actual Binding]onUserIdChanged:" + s);
                        }
                    }, new OnUniqueCodeApiListener() {
                        @Override
                        public void onSuccessSendUniqueCode(UsersResponse usersResponse) {
                            Log.d(getClass().getSimpleName(), "[Actual Binding]onSuccessSendUniqueCode:" + s);
                            onSendUniqueCodeSuccess();
                        }



                        @Override
                        public void onFailSendUniqueCode(String s, int i) {
                            //Firebase　イベントをレポートする
                            String errContent = "ユーザー識別子登録失敗、"+ s+ "、コード "+ i;
                            String nextAction = "ログイン画面へ遷移する";
                            FirebaseUtil.logEventFunnelPushOption(mContext, getLocalClassName(), errContent, nextAction);
                            onSendUniqueCodeFail(s);
                        }

                    });
        } else {
            goToLogin();
        }

    }

    private synchronized void onSendUniqueCodeFail(String s) {
        removeHandlerMessageIfNeed();
        if (mIsHandlingTimeOut) {
            Log.d(getClass().getSimpleName(), "onFailSendUniqueCode:" + s);
            return;
        }
        goToLogin();
    }

    private synchronized void onSendUniqueCodeSuccess() {
        removeHandlerMessageIfNeed();
        if (mIsHandlingTimeOut) {
            Log.d(getClass().getSimpleName(), "onSuccessSendUniqueCode");
            return;
        }
        doAppLogin();
    }

    private void removeHandlerMessageIfNeed() {
        if (mSplashHandler != null) {
            mSplashHandler.removeMessages(SplashHandler.SET_UNIQUE_CODE_TIME_OUT);
        }
    }

    private void doAppLogin() {
        // 認証チェック
        String mailAddress = "";
        String password = "";

        mailAddress = ((LMApplication) getApplicationContext()).getAccountId();
        password = ((LMApplication) getApplicationContext()).getPassword();


        if (password.isEmpty()) {
            //ログアウトされているのでログイン画面へ遷移
            goToLogin();
            finish();
        } else {
            // 実行
            RetrofitUtil.getInstance().getAPI()
                    .appLogin(mailAddress, password)
                    .compose(RxUtil.<LoginResult>observableSchedulerHelper())
                    .map(new Function<LoginResult, LoginResult.Response>() {
                        @Override
                        public LoginResult.Response apply(LoginResult loginResult) throws Exception {
                            return loginResult.response;
                        }
                    })
                    .subscribe(new BaseObserver<LoginResult.Response>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {
                            mCompositeDisposable.add(disposable);
                        }

                        @Override
                        public void onNext(LoginResult.Response response) {
                            if (AccountUtils.chkSuccessfullyLoggedIn(mContext, response)) {
                                //ログイン成功
                                goToServiceTop();
                            } else {
                                //Firebase　イベントをレポートする
                                String auth_result = null, pay_status = null;
                                if(response != null){
                                    auth_result = response.authResult;
                                    pay_status = response.payStatus;
                                }
                                String errContent = "認定結果判定失敗("+ auth_result+ ", "+ pay_status+ ")";
                                String nextAction = "ログイン画面へ遷移する";
                                FirebaseUtil.logEventLoginResult(mContext, getLocalClassName(),  errContent, nextAction);
                                goToLogin();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            //Firebase　イベントをレポートする
                            String nextAction = "ログイン画面へ遷移する";
                            if(throwable instanceof JsonParseException ||
                                    throwable instanceof MalformedJsonException ||
                                    throwable instanceof JSONException ||
                                    throwable instanceof JsonParseException ||
                                    throwable instanceof EOFException) {
                                FirebaseUtil.logEventLoginData(mContext, getLocalClassName(), throwable, nextAction);
                            }else{
                                FirebaseUtil.logEventLoginCommunication(mContext, getLocalClassName(), throwable, nextAction);
                            }
                            goToLogin();
                        }
                    });
        }
    }


    /**
     * ログイン画面へ遷移します。
     */
    private void goToLogin() {
        LogUtil.logDebug("start:" + mPushParamUrl);
        doUpdatePushMessageStatus();

        Intent intent = new Intent(this, LoginActivity.class);

        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl);
        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL, mPushParamUrl);
        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_ID, mPushParamMessageId);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * サービストップへ遷移します。
     */
    private void goToServiceTop() {

        LogUtil.logDebug("start:" + mPushParamUrl);
        doUpdatePushMessageStatus();

        Intent intent;
        boolean isQuestionDone = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getQuestionDone();
        if (isQuestionDone) {
            //アンケート登録画面で登録または後で登録を選択済み
            intent = new Intent(this, HomeActivity.class);
        } else {
            //未だアンケート登録画面で登録または後で登録を選択していない
            intent = new Intent(this, UserInfoActivity.class);
        }

        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL, mPushParamUrl);
        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_ID, mPushParamMessageId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void doUpdatePushMessageStatus() {
        if (mPushParamMessageId != null && !mPushParamMessageId.isEmpty()) {
            Log.d(getClass().getSimpleName(), "[既読処理実行]" + mPushParamMessageId);
            //対象のメッセージを既読にする為、メッセージ詳細の取得を行う（FunnelPushの「メッセージ詳細を取得するとサーバ側で自動的に既読状態に変更されます。」という仕様に従っています。）
            FunnelPush.getMessagesDetail(getApplicationContext(), mPushParamMessageId, new OnMessagesDetailApiListener() {
                @Override
                public void onSuccessGetMessageDetail(MessagesResponse response) {
                    final MessagesListResponse.MessageSummary msg = response.getMessage(); //msgの内容は一覧と同様
                    Log.d(getClass().getSimpleName(), "[既読成功]" + msg.getTitle());
                }

                @Override
                public void onFailGetMessageDetail(String errorMessage, int statusCode) { //取得エラー時
                    Log.d(getClass().getSimpleName(), "[既読失敗]" + errorMessage);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
        mSplashHandler = null;
    }


    private static class SplashHandler extends Handler {

        private static final int SET_UNIQUE_CODE_TIME_OUT = 1;
        private static final int SET_UNIQUE_CODE_TIME_OUT_DELAY = 5 * 1000;

        private WeakReference<SplashActivity> mWeakReference;

        private SplashHandler(SplashActivity activity) {
            super();
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SET_UNIQUE_CODE_TIME_OUT) {
                SplashActivity splashActivity = mWeakReference.get();
                if (splashActivity != null) {
                    splashActivity.handleSetUniqueCodeTimeOut();
                }
            }
        }
    }

    private synchronized void handleSetUniqueCodeTimeOut() {
        LogUtil.logDebug("tryHandleCount: " + mTryHandleCount);
        if (mIsHandlingTimeOut) {
            Log.d(getClass().getSimpleName(), "is handling message");
            return;
        }
        mIsHandlingTimeOut = true;
        if (mTryHandleCount < MAX_TRY_COUNT) {
            doScreenTransition();
        } else {
            goToLogin();
        }
        ++mTryHandleCount;
    }
}
