package jp.co.lhe.lhememberapp.ui.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.utils.NetworkUtils;

/**
 * 共通WebView
 */
public class ModalWebView extends RelativeLayout {

    private Context mContext;

    private String mUrl;
    private boolean mNeedSso;
    private String mExScreen;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private SwipeRefreshLayout mSrl;
    private View mRootView;

    /**
     * constructor
     *
     * @param context
     * @param url:表示するページのURL
     * @param needSso:SSO認証が必要かどうか
     */
    public ModalWebView(Context context, String url, boolean needSso, String exScreen) {
        super(context);
        init(context, url, needSso, exScreen);
    }

    public ModalWebView(Context context, String url, boolean needSso, String exScreen, boolean isRefresh) {
        super(context);
        init(context, url, needSso, exScreen, isRefresh);
    }


    /**
     * constructor
     *
     * @param context
     * @param attrs
     * @param url:表示するページのURL
     * @param needSso:SSO認証が必要かどうか
     */
    public ModalWebView(Context context, AttributeSet attrs, String url, boolean needSso, String exScreen) {
        super(context, attrs);
        init(context, url, needSso, exScreen);
    }

    /**
     * constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param url:表示するページのURL
     * @param needSso:SSO認証が必要かどうか
     */
    public ModalWebView(Context context, AttributeSet attrs, int defStyleAttr, String url, boolean needSso, String exScreen) {
        super(context, attrs, defStyleAttr);
        init(context, url, needSso, exScreen);
    }

    private void init(Context context, String url, boolean needSso, String exScreen) {
        init(context, url, needSso, exScreen, true);
    }

    /**
     * 初期処理を行います。
     *
     * @param context
     * @param url
     * @param needSso
     */
    private void init(Context context, String url, boolean needSso, String exScreen, boolean isRefresh) {
        this.mContext = context;
        this.mUrl = url;
        this.mNeedSso = needSso;
        this.mExScreen = exScreen;

        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + url);

        mRootView = View.inflate(mContext, R.layout.component_web_view, this);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.content_web_view_progress_bar);
        mWebView = (WebView) mRootView.findViewById(R.id.modal_web_view);
        ImageButton browserBackBtn = (ImageButton) mRootView.findViewById(R.id.modal_action_bar_back_btn);
        browserBackBtn.setVisibility(View.GONE);
        ImageButton browserForwardBtn = (ImageButton) mRootView.findViewById(R.id.modal_action_bar_forward_btn);
        browserForwardBtn.setVisibility(View.GONE);
        //new webview controller start
        ImageView backIv = (ImageView) mRootView.findViewById(R.id.iv_wv_back);
        ImageView forwardIv = (ImageView) mRootView.findViewById(R.id.iv_wv_forward);

        backIv.setEnabled(false);
        forwardIv.setEnabled(false);
        mSrl = mRootView.findViewById(R.id.srl);
        mSrl.setEnabled(isRefresh);
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
                mProgressBar.setVisibility(GONE);
            }
        });
        //new webview controller end
        setWebView(backIv, forwardIv);
    }


    private void setWebView(ImageView backIv, ImageView forwardIv) {
        if (!NetworkUtils.chkOnline(mContext)) {
            EventBusHolder.EVENT_BUS.post(new OnCalledViewCloseEvent(mExScreen));
            return;
        }
        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent + " lhe-app");
        mWebView.setWebViewClient(new ContentsWebViewClient(mRootView,mProgressBar, backIv, forwardIv, mSrl, mNeedSso));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(mUrl);
    }

    public void destoryWebView() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }
}
