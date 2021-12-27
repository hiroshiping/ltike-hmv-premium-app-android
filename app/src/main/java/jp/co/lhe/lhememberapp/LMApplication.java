package jp.co.lhe.lhememberapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.webkit.WebSettings;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.co.lhe.lhememberapp.utils.EncryptUtil;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnFunnelPushInitializeListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * PremiumMember APPのApplicationクラス
 */

public class LMApplication extends MultiDexApplication implements
        Application.ActivityLifecycleCallbacks {

    private OkHttpClient mOkHttpClient;

    private LMASharedPreference mLmaSharedPreference;
    private static LMApplication mApplication;
    private static Activity mActivity;
    private static final int HTTP_CONNECTION_TIMEOUT = Constants.TIME_OUT;
    private Handler mHandler;
    private int mMainId;
    private String TAG="LMApplication";

    /**
     * lcus noを取得します。
     *
     * @return
     */
    public String getLcusNo() {
        return getLmaSharedPreference().getLcusNo();
    }

    /**
     * lcus noを保存します。
     *
     * @param lcusNo
     */
    public void setLcusNo(String lcusNo) {
        getLmaSharedPreference().setLcusNo(lcusNo);
    }

    /**
     * ログインID（email）を取得します。
     *
     * @return
     */
    public String getAccountId() {
        String email = getLmaSharedPreference().getAccountId();
        String accountId = "";
        if (email != null && !email.isEmpty()) {
            try {
                accountId = EncryptUtil.decryptString(this, email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return accountId;
    }

    /**
     * ログインID（email）を保存します。
     *
     * @param accountId
     */
    public void setAccountId(String accountId) {
        String email = "";
        try {
            //暗号化
            email = EncryptUtil.encryptString(getApplicationContext(), accountId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLmaSharedPreference().setAccountId(email);

    }

    /**
     * ログインパスワードを取得します。
     *
     * @return
     */
    public String getPassword() {
        String pw = getLmaSharedPreference().getAccountPw();
        String accountPw = "";
        if (pw != null && !pw.isEmpty()) {
            try {
                accountPw = EncryptUtil.decryptString(this, pw);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return accountPw;
    }

    /**
     * ログインパスワードを保存します。
     *
     * @param password
     */
    public void setPassword(String password) {
        String pw = "";
        try {
            //暗号化
            pw = EncryptUtil.encryptString(getApplicationContext(), password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLmaSharedPreference().setAccountPw(pw);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, " onActivityCreated:------------------------- ");

    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, " onActivityStarted:------------------------- ");

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, " onActivityResumed:------------------------- ");
        mActivity = activity;
        initFunnelPush();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, " onActivityPaused:------------------------- ");
        FunnelPush.onDestroy(getApplicationContext());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, " onActivityStopped:------------------------- ");

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, " onActivitySaveInstanceState:------------------------- ");

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, " onActivityDestroyed:------------------------- ");

    }


    /**
     * HttpClientを取得します。
     *
     * @return httpclientを返却します。
     */
    public OkHttpClient getOkhttpClient() {
        if (mOkHttpClient == null) {
            setOkhttpClient();
        }
        return mOkHttpClient;
    }

    /**
     * http clientを初期化します。
     */
    private void setOkhttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.followRedirects(true);
        httpClient.followSslRedirects(true);
        httpClient.readTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        if (getApplicationContext().getString(R.string.app_mode_develop).equals(getString(R.string.app_mode))) {
            //開発環境の場合のみ、証明書を無視
            final TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            // 特に何もしない
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            // 特に何もしない
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };
            try {
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManagers, new java.security.SecureRandom());
//                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                httpClient.sslSocketFactory(createSSLSocketFactory());
                httpClient.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // ホスト名の検証を行わない
                        return true;
                    }
                });
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }

        httpClient.retryOnConnectionFailure(true).build();

        //developの場合のみ　ログ出力設定
        if (getString(R.string.app_mode).equals(getString(R.string.app_mode_develop))) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
        //202007以後検証環境HttpsのUser-Agentがリセットする
        if(BuildConfig.FLAVOR.equals("staging")){
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .removeHeader("User-Agent")//古いAgent削除
                            .addHeader("User-Agent", "LheMemberApp")//新たのAgent追加
                            .build();
                    return chain.proceed(request);
                }
            });
        }

        //クッキー管理
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        //クライアント生成
        mOkHttpClient = httpClient.cookieJar(cookieJar).build();


    }

    /**
     * SSLのオレオレ証明書を許可するための準備
     *
     * @return SSLSocketFactory
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mHandler = new Handler();
        mMainId = Process.myTid();
        initLogger();
        initFirebase();
        registerActivityLifecycleCallbacks(this);
    }

    private void initFirebase() {
        if (BuildConfig.FLAVOR.equals("product") || BuildConfig.FLAVOR.equals("staging")) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        }
        // FireStore安全ルール使用の為、認証が必要です
        FirebaseUtil.signInFirebaseAuth();
    }

    public static LMApplication getApplication() {
        return mApplication;
    }

    private void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public LMASharedPreference getLmaSharedPreference() {
        if (mLmaSharedPreference == null) {
            mLmaSharedPreference = new LMASharedPreference(this);
        }
        return mLmaSharedPreference;
    }

    public static Activity getCurrentActivty() {
        return mActivity;
    }

    public void runOnMainThread(Runnable r) {
        if (Process.myPid() == mMainId) {
            r.run();
        } else {
            mHandler.post(r);
        }
    }

    private void initFunnelPush() {
        FunnelPush.enableLocation(getApplicationContext(), false);
        FunnelPush.enableDevelopmentMode(getApplicationContext(), false);
        Log.d(TAG, ">>>>>>>>>>>>>>>>>> [Push.oncreate]");
        FunnelPush.onCreate(getApplicationContext(), getString(R.string.funnel_push_app_key), getString(R.string.firebase_senderid), new OnFunnelPushInitializeListener() {
            @Override
            public void onFunnelPushReady() {
                boolean allowPush = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationFlg();
                Log.d(TAG, ">>>>>>>>>>>>>>>>>> [Push通知設定]" + allowPush);
                if (allowPush) {
                    FunnelPush.enablePushNotification(getApplicationContext(), true);
                }
                Log.e(TAG, "success");
            }

            @Override
            public void onInitializeFailed(String s, int i) {
                Log.e(TAG, "fail");
            }
        });

    }
}
