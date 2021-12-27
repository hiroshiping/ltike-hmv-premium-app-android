package jp.co.lhe.lhememberapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.io.EOFException;
import java.text.ParseException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.BaseObserver;
import jp.co.lhe.lhememberapp.network.RetrofitUtil;
import jp.co.lhe.lhememberapp.network.models.AppLoginModel;
import jp.co.lhe.lhememberapp.network.models.LoginResult;
import jp.co.lhe.lhememberapp.network.models.ToastInfo;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.utils.AccountUtils;
import jp.co.lhe.lhememberapp.ui.utils.NetworkUtils;
import jp.co.lhe.lhememberapp.ui.views.ModalWebView;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.co.lhe.lhememberapp.utils.PushUtil;
import jp.co.lhe.lhememberapp.utils.RxUtil;
import jp.co.lhe.lhememberapp.utils.SharedPrefsUtils;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnUniqueCodeApiListener;
import jp.funnelpush.sdk.callback.OnUserAttributesSendListener;
import jp.funnelpush.sdk.callback.OnUserIdChangedListener;
import jp.funnelpush.sdk.model.builder.FunnelPushBuilders;
import jp.funnelpush.sdk.response.UserAttributes;
import jp.funnelpush.sdk.response.UsersResponse;

/**
 * Activity
 * 01-03.起動_ログイン画面
 */
public class LoginActivity extends BaseActivity {

    private EditText mMailText;
    private EditText mPasswordText;

    private FrameLayout mContentsView;
    private ModalWebView mModalWebView;

    private String mPushParamUrl = "";
    private boolean mDoingLogin = false;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;
    private static String FIRST_TIME = "first_time";
    private TextView tv_first;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCompositeDisposable = new CompositeDisposable();
        mContext = LoginActivity.this;

        //Push param
        mPushParamUrl = getIntent().getStringExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);
        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl );

        mMailText = (EditText) findViewById(R.id.activity_login_mail_text);
        mPasswordText = (EditText) findViewById(R.id.activity_login_password_text);
        mContentsView = (FrameLayout) findViewById(R.id.activity_login_contents_view);
        tv_first = (TextView) findViewById(R.id.tv_first);

        mDoingLogin = true;
        // ログインボタン
        Button loginBtn = (Button) findViewById(R.id.activity_login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        final AppLoginModel model = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getAppLoginModel();
        //お困りの場合
        LinearLayout problemBtn = (LinearLayout) findViewById(R.id.activity_login_problems_btn);
        problemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openWebView(model.loginHelp.href, false);
            }
        });
        TextView mPrivacyTv = findViewById(R.id.tv_privacy);
        mPrivacyTv.setText(Html.fromHtml(getString(R.string.policy_hint)));

        mPrivacyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView(getString(R.string.policy_url), false);
            }
        });


        //サービス内容を見る
        Button tutorialBtn = (Button) findViewById(R.id.activity_login_tutorial_btn);
        tutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView(model.tutorial.href, false);
            }
        });

    }

    private void startLogin() {
        if (!mDoingLogin) {
            Logger.d("goto !mDoingLogin");
            FirebaseUtil.logEventunKnown(mContext, getLocalClassName(), "ログインを繰り返す", "アクションなし");
            return;
        }

        final String mailAddress = mMailText.getText().toString();
        final String password = mPasswordText.getText().toString();
        if (TextUtils.isEmpty(mailAddress) || TextUtils.isEmpty(password)) {
            loginNg();
            String nextAction = FirebaseUtil.getActionDialog(
                    getString(R.string.error_login_title),
                    getString(R.string.error_login_message));
            FirebaseUtil.logEventunKnown(mContext, getLocalClassName(), "ログインアカウント、パスワードなし", nextAction);
            return;
        }

        //ログイン処理
        RetrofitUtil.getInstance()
                .getAPI()
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
                            Logger.d("AccountUtils.chkSuccessfullyLoggedIn true");
                            //ログイン成功
                            String lcusNo = response.lcusNo;
                            AccountUtils.loginPostProcessing(mContext, response);
                            loginOk(mailAddress, password, lcusNo);
                        } else {
                            Logger.d("AccountUtils.chkSuccessfullyLoggedIn false");
                            //Firebase　イベントをレポートする
                            String auth_result = null, pay_status = null;
                            if(response != null){
                                auth_result = response.authResult;
                                pay_status = response.payStatus;
                            }
                            String errContent = "認定結果判定失敗("+ auth_result+ ", "+ pay_status+ ")";
                            String nextAction = FirebaseUtil.getActionDialog(getString(R.string.error_login_title),
                                    getString(R.string.error_login_message));
                            FirebaseUtil.logEventLoginResult(mContext, getLocalClassName(),  errContent, nextAction);
                            loginNg();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        //Firebase　イベントをレポートする
                        String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                                ToastInfo.getNetErrorInfo().getMessage());
                        if(throwable instanceof JsonParseException ||
                                throwable instanceof MalformedJsonException ||
                                throwable instanceof JSONException ||
                                throwable instanceof JsonParseException ||
                                throwable instanceof EOFException) {
                            FirebaseUtil.logEventLoginData(mContext, getLocalClassName(), throwable, nextAction);
                        }else{
                            FirebaseUtil.logEventLoginCommunication(mContext, getLocalClassName(), throwable, nextAction);
                        }
                        showNetToast(ToastInfo.getNetErrorInfo());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    private void showNetToast(ToastInfo toastInfo, boolean isInitPush) {
        if (isInitPush) {
            FunnelPush.onCreate(getApplicationContext(), getString(R.string.funnel_push_app_key), getString(R.string.firebase_senderid));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(toastInfo.getTitle())
                .setMessage(toastInfo.getMessage())
                .setCancelable(false)
                .setNegativeButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startLogin();
                    }
                })
                .show();
        return;
    }

    /**
     * エラー内容をToastで表示します。
     *
     * @param toastInfo
     */
    private void showNetToast(ToastInfo toastInfo) {
        showNetToast(toastInfo, false);
    }

    private void checkFirstTimeRunning() {
        boolean isFirstTime = SharedPrefsUtils.getBooleanPreference(this, FIRST_TIME, true);
        if(isFirstTime){
            tv_first.setVisibility(View.VISIBLE);
            SharedPrefsUtils.setBooleanPreference(this,FIRST_TIME,false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFirstTimeRunning();
        mPushParamUrl = getIntent().getStringExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);
        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl );


        if (mContentsView != null && mContentsView.getVisibility() == View.VISIBLE) {
            //バックグラウンドから復帰した時にwebview表示中だった場合、タイムアウトチェックを行う
            long timeoutMinutes = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getOperationWebViewTimeOutTime();
            long lastOperationTime = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getLastOperationOnWebViewTime();
            if (timeoutMinutes == 0) {
                return;
            }

            try {
                //一定時間操作がなかった場合はログイン画面へ遷移
                if (DateUtil.isTimeOver(timeoutMinutes, lastOperationTime)) {
                    //ログイン画面へ遷移
                    new AlertDialog.Builder(this)
                            .setTitle("確認")
                            .setMessage(getString(R.string.error_view_timeout))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //WebView表示画面からWebViewTimeoutSecに設定された時間以上かつ認証情報未保持の場合、ログイン画面へ遷移する
                                    Intent intent = new Intent(mContentsView.getContext(), SplashActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    //アプリログイン済フラグを送信
                    sendLoginFlgToFunnelPush();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //アプリログイン済フラグを送信
            sendLoginFlgToFunnelPush();
        }

        if (mPushParamUrl != null && !mPushParamUrl.isEmpty()) {
            //未ログイン状態の時にPUSH通知タップで起動された場合
            openWebView(mPushParamUrl, true);
            mPushParamUrl = "";
            getIntent().removeExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);

        }

    }

    /**
     * アプリログイン済フラグを送信します。
     */
    private void sendLoginFlgToFunnelPush() {
        boolean isAlreadyLoggedIn = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getAppLoggedInFlg();
        if (!isAlreadyLoggedIn) {
            //１度もログインしていない場合、アプリログイン済フラグを"未ログイン"で送信する
            //Builderの生成
            FunnelPushBuilders.UserAttributesBuilder builder =
                    FunnelPushBuilders.newUserAttributesBuilderInstance(getApplicationContext());

            //アプリログイン済フラグ:未ログイン
            builder.setAttributes(UserAttributes.ATTR_A, 3, 0);

            //builderの送信（非同期なので完了通知を受け取る場合はListenerを設定する。nullも可）
            FunnelPush.sendUserAttributes(getApplicationContext(), builder, new OnUserAttributesSendListener() {

                @Override
                public void onSuccessSendUserAttribute(UserAttributes userAttributes) {
                    Log.d(getClass().getSimpleName(), "[ユーザー属性登録]アプリログイン済フラグ(未ログイン)を送信しました。:" + userAttributes.getUserAttribute().getUserId());
                }

                @Override
                public void onFailSendUserAttribute(String s, int i) {
                    Logger.d(s);
                }
            });
        }
    }

    private void loginNg() {
        Logger.d("loginNg start");
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_login_title))
                .setMessage(getString(R.string.error_login_message))
                .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mDoingLogin = true;
                    }
                })
                .show();
    }

    /**
     * ログイン後の処理を行います。
     *
     * @param mailAddress
     * @param password
     */
    private void loginOk(final String mailAddress, final String password, final String lcusNo) {
        Logger.d("loginOk start");
        boolean loggedInFlg = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getAppLoggedInFlg();
        Logger.d(loggedInFlg);
        if (!loggedInFlg) {
            //初めてのログインの場合、ユーザー属性登録を行う

            //Builderの生成
            FunnelPushBuilders.UserAttributesBuilder builder =
                    FunnelPushBuilders.newUserAttributesBuilderInstance(getApplicationContext());

            //アプリログイン済フラグ
            builder.setAttributes(UserAttributes.ATTR_A, 3, 1);

            if (!PushUtil.getInstance().isInited()) {
                showNetToast(ToastInfo.getNetErrorInfo(), true);
                //Firebase　イベントをレポートする
                String errContent = "FunnelPush初期化失敗";
                String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                        ToastInfo.getNetErrorInfo().getMessage());
                FirebaseUtil.logEventFunnelPushOption(mContext, getLocalClassName(), errContent, nextAction);
                return;
            }
            //Rate属性の設定（numberには1～50を指定）
            //builderの送信（非同期なので完了通知を受け取る場合はListenerを設定する。nullも可）
            Logger.d("FunnelPush.sendUserAttributes start");
            FunnelPush.sendUserAttributes(getApplicationContext(), builder, new OnUserAttributesSendListener() {

                @Override
                public void onSuccessSendUserAttribute(UserAttributes userAttributes) {
                    Logger.d("FunnelPush.sendUserAttributes  onSuccessSendUserAttribute callback start");
                    ((LMApplication) getApplicationContext()).getLmaSharedPreference().setAppLoggedInFlg(true);
                    loginContinue(lcusNo, mailAddress, password);

                }

                @Override
                public void onFailSendUserAttribute(String s, int i) {
                    Logger.d("FunnelPush.sendUserAttributes  onFailSendUserAttribute callback start");
                    //Firebase　イベントをレポートする
                    String errContent = "ユーザー属性設定失敗、"+ s+ "、 コード "+ i;
                    String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                            ToastInfo.getNetErrorInfo().getMessage());
                    FirebaseUtil.logEventFunnelPushOption(mContext, getLocalClassName(), errContent, nextAction);
                    showNetToast(ToastInfo.getNetErrorInfo(), true);
                }
            });
        } else {
            loginContinue(lcusNo, mailAddress, password);
        }
    }

    private void loginContinue(final String lcusNo, final String mailAddress, final String password) {
        if (!PushUtil.getInstance().isInited()) {
            showNetToast(ToastInfo.getNetErrorInfo(), true);
            //Firebase イベントをレポートする
            String errContent = "FunnelPush初期化失敗";
            String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                    ToastInfo.getNetErrorInfo().getMessage());
            FirebaseUtil.logEventFunnelPushOption(mContext, getLocalClassName(), errContent, nextAction);
            return;
        }

        // Actual Binding
        FunnelPush.sendUniqueCode(getApplicationContext(), lcusNo,
                FunnelPush.UniqueCodeType.FP_ORIGINAL, new OnUserIdChangedListener() {
                    @Override
                    public void onUserIdChanged(String s) {

                        Log.d(getClass().getSimpleName(), "[Actual Binding]ユーザーIDが変更されました。:" + s);
                    }
                }, new OnUniqueCodeApiListener() {
                    @Override
                    public void onSuccessSendUniqueCode(UsersResponse usersResponse) {
                        ((LMApplication) getApplicationContext()).setAccountId(mailAddress);
                        ((LMApplication) getApplicationContext()).setPassword(password);
                        ((LMApplication) getApplicationContext()).setLcusNo(lcusNo);

                        boolean isQuestionDone = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getQuestionDone();
                        Intent intent;
                        if (isQuestionDone) {
                            //アンケート登録画面で登録または後で登録を選択済み
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        } else {
                            //未だアンケート登録画面で登録または後で登録を選択していない
                            intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                        }
                        intent.putExtra(getString(R.string.user_info_param), getString(R.string.user_info_ex_screen_login));

//                        //PUSH通知から起動された場合
//                        if (mPushParamUrl != null && !mPushParamUrl.isEmpty()) {
//                            intent.putExtra(getString(R.string.gcm_param_url), mPushParamUrl);
//                            Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl);
//                        }
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailSendUniqueCode(String s, int i) {
                        Logger.d(s);
                        //Firebase イベントをレポートする
                        String errContent = "ユーザー識別子登録失敗、"+ s+ "、コード "+ i;
                        String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                                ToastInfo.getNetErrorInfo().getMessage());
                        FirebaseUtil.logEventFunnelPushOption(mContext, getLocalClassName(), errContent, nextAction);
                        showNetToast(ToastInfo.getNetErrorInfo(), true);
                    }
                });

    }

    /**
     * WebViewに対象のページを表示します。
     *
     * @param url
     * @param needSso
     */
    private void openWebView(String url, boolean needSso) {

        if (!NetworkUtils.chkOnline(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.service_top_error_dialog_title))
                    .setMessage(getString(R.string.service_top_error_dialog_message))
                    .setPositiveButton(getString(R.string.service_top_error_dialog_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        }
        Log.d(getClass().getSimpleName(), "@openWebView ▲▼▲▼▲▼▲▼▲ url is " + url);
        mModalWebView = new ModalWebView(LoginActivity.this, url, needSso, OnCalledViewCloseEvent.SCREEN_LOGIN, false);
        mContentsView.removeAllViews();
        mContentsView.addView(mModalWebView);
        mContentsView.setVisibility(View.VISIBLE);

        ImageButton backBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_left_btn);
        backBtn.setVisibility(View.GONE);
        ImageButton closeBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_right_btn);
        closeBtn.setVisibility(View.VISIBLE);
        final ImageButton browserBackBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_back_btn);
        final ImageButton browserForwardBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_forward_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentsView.setVisibility(View.GONE);
            }
        });

        mModalWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //< >ボタンの設定
        final WebView webView = (WebView) mModalWebView.findViewById(R.id.modal_web_view);
        browserBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        browserForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        //new wv controller start
        ImageView backIv = (ImageView) mModalWebView.findViewById(R.id.iv_wv_back);
        ImageView forwardIv = (ImageView) mModalWebView.findViewById(R.id.iv_wv_forward);
        ImageView closeIv = (ImageView) mModalWebView.findViewById(R.id.iv_wv_close);
        backIv.setEnabled(false);
        forwardIv.setEnabled(false);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentsView.setVisibility(View.GONE);
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        forwardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });
        //new wv controller end

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mContentsView.getVisibility() == View.VISIBLE) {
                mContentsView.setVisibility(View.GONE);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("アプリケーションの終了")
                        .setMessage("アプリケーションを終了してよろしいですか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        }
        return true;
    }
}
