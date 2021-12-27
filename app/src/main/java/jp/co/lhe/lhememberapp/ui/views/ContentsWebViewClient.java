package jp.co.lhe.lhememberapp.ui.views;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnTitleChangeEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.DateUtil;

public class ContentsWebViewClient extends WebViewClient {
    private final static String TAG = "wv";
    private ProgressBar mProgressBar;
    private ImageView mBackImageButton;
    private ImageView mForwardImageButton;
    private SwipeRefreshLayout mSrl;
    private boolean mNeedSso;
    private String mRealUrl;
    private boolean mLoginFlag;
    private Handler mHandle;
    private boolean mFirstTimeLoginFlag = true;
    private String[] mHideViewList = LMApplication.getApplication().getResources().getStringArray(R.array.sso_url);
    private View mRootView;

    public ContentsWebViewClient(View rootView, ProgressBar progressBar, ImageView backImageButton, ImageView forwardImageButton, SwipeRefreshLayout srl, boolean needSso) {
        this.mRootView = rootView;
        this.mProgressBar = progressBar;
        this.mBackImageButton = backImageButton;
        this.mForwardImageButton = forwardImageButton;
        this.mSrl = srl;
        this.mNeedSso = needSso;
        mHandle = new Handler();
    }


    @Override
    public void onPageStarted(final WebView view, String url, Bitmap favicon) {

        if (view == null) {
            return;
        }

        //logout
        if (LMApplication.getApplication().getResources().getString(R.string.sso_logout_url).equals(url)) {
            mFirstTimeLoginFlag = false;
        }
        Log.d(TAG, "onPageStarted webviewId:" + System.identityHashCode(view) + " clientId:" + this.toString() + " mLoginFlag:" + mLoginFlag + " url :" + url);

        //hide login page
        if (mFirstTimeLoginFlag && isLogin(url)) {
            showLoginView();
            mLoginFlag = true;
        } else {
            mRealUrl = url;
            mProgressBar.setVisibility(View.VISIBLE);
        }

        setBackFowardButtonFace(view, mBackImageButton, mForwardImageButton);
        if (view.isShown()) {
            EventBusHolder.EVENT_BUS.post(new OnTitleChangeEvent(""));
        }
    }

    private void finishLogin(WebView view) {
        synchronized (ContentsWebViewClient.class) {
            if (mLoginFlag) {
                hideLoginView();
                mRealUrl = null;
                mLoginFlag = false;
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        if (view == null) {
            return;
        }

        if (!TextUtils.isEmpty(mRealUrl) && mRealUrl.equals(url)) {
            finishLogin(view);
        }
        Log.d(TAG, "onPageFinished webviewId:" + System.identityHashCode(view) + " clientId:" + this.toString() + " mLoginFlag:" + mLoginFlag + " url :" + url);

//        Logger.d("mNeedSso:" + mNeedSso + "webview Url:" + url);
        if (mLoginFlag && mFirstTimeLoginFlag && mNeedSso && !TextUtils.isEmpty(url) && url.contains(LMApplication.getApplication().getResources().getString(R.string.sso_login_url))) {
            // テキストフィールドに入力するJSを実行する
            String accountId = LMApplication.getApplication().getAccountId();
            String password = LMApplication.getApplication().getPassword();
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(password)) {
                String js1 = "document.forms.loginform.EMAIL.value='" + accountId + "'";
                String js2 = "document.forms.loginform.PASSWORD.value='" + password + "'";
                String js3 = "document.forms.loginform.submit();";
                executeJs(view, js1);
                executeJs(view, js2);
                executeJs(view, js3);
                mFirstTimeLoginFlag = false;
            } else {
                hideLoginView();
            }
        } else {
            hideLoginView();
        }


        mProgressBar.setVisibility(View.GONE);
        mSrl.setRefreshing(false);
        setBackFowardButtonFace(view, mBackImageButton, mForwardImageButton);
        //最終WebView操作日時を保存
        DateUtil.setLastOperationOnWebViewTime(view.getContext().getApplicationContext());
        if (view.isShown()) {
            EventBusHolder.EVENT_BUS.post(new OnTitleChangeEvent(view.getTitle()));
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        hideLoginView();
    }


    private void executeJs(WebView view, String jsCode) {
        final int version = Build.VERSION.SDK_INT;
        if (version < Build.VERSION_CODES.KITKAT) {
            view.loadUrl(jsCode);
        } else {
            view.evaluateJavascript(jsCode, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // useless
                }
            });
        }

    }

    /**
     * ボタンのアイコンの状態を変更します。
     *
     * @param webView
     * @param backImageButton
     * @param forwardImageButton
     */
    private void setBackFowardButtonFace(WebView webView, ImageView backImageButton, ImageView forwardImageButton) {
        backImageButton.setEnabled(webView.canGoBack());
        forwardImageButton.setEnabled(webView.canGoForward());
    }

    private int getWebViewVisibility(String url) {
        if (TextUtils.isEmpty(url)) {
            return View.VISIBLE;
        }
        for (String item : mHideViewList) {
            if (url.contains(item)) {
                return View.INVISIBLE;
            }
        }
        return View.VISIBLE;
    }

    private boolean isLogin(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        for (String item : mHideViewList) {
            if (url.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private void showLoginView() {
        mRootView.findViewById(R.id.rl_login).setVisibility(View.VISIBLE);
        ImageView loginIv = mRootView.findViewById(R.id.iv_login);
        Glide.with(mRootView).asGif().load(R.drawable.login_anim).into(loginIv);
    }

    private void hideLoginView() {
        mRootView.findViewById(R.id.rl_login).setVisibility(View.GONE);
    }
}
