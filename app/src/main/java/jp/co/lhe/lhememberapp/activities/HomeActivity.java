package jp.co.lhe.lhememberapp.activities;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.logger.Logger;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import jp.co.lhe.lhememberapp.BuildConfig;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.fragments.BaseFragment;
import jp.co.lhe.lhememberapp.fragments.LicenseFragment;
import jp.co.lhe.lhememberapp.fragments.NoticeDetailFragment;
import jp.co.lhe.lhememberapp.fragments.NoticeFragment;
import jp.co.lhe.lhememberapp.fragments.PushSettingFragment;
import jp.co.lhe.lhememberapp.fragments.QuestionFragment;
import jp.co.lhe.lhememberapp.fragments.ServiceTopFragment;
import jp.co.lhe.lhememberapp.fragments.SettingFragment;
import jp.co.lhe.lhememberapp.fragments.TermsFragment;
import jp.co.lhe.lhememberapp.network.models.AppTopModel;
import jp.co.lhe.lhememberapp.ui.adapters.MenuAdapter;
import jp.co.lhe.lhememberapp.ui.adapters.SettingAdapter;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnLicensePageEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnNoticeCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnNoticeDetailCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnNoticeItemTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnPushSettingTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnQuestionTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnRefreshNoticeBadgeEvent;
import jp.co.lhe.lhememberapp.ui.events.OnSSOLoginFailedEvent;
import jp.co.lhe.lhememberapp.ui.events.OnSettingMenuCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnTitleChangeEvent;
import jp.co.lhe.lhememberapp.ui.models.MenuItem;
import jp.co.lhe.lhememberapp.ui.utils.AccountUtils;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.views.ModalWebView;
import jp.co.lhe.lhememberapp.ui.views.PopUpAlertDialog;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.co.lhe.lhememberapp.utils.ScreenUtil;
import jp.co.lhe.lhememberapp.utils.SharedPrefsUtils;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnMessagesListApiListener;
import jp.funnelpush.sdk.callback.OnUserAttributesApiListener;
import jp.funnelpush.sdk.response.MessagesListResponse;
import jp.funnelpush.sdk.response.UserAttributes;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class HomeActivity extends BaseActivity implements
        ServiceTopFragment.OnFragmentInteractionListener, TermsFragment.OnFragmentInteractionListener, NoticeFragment.OnFragmentInteractionListener,
        PushSettingFragment.OnFragmentInteractionListener, QuestionFragment.OnFragmentInteractionListener {

    // お知らせ詳細
    private static final String FRAGMENT_NOTICE_DETAIL = "frag_notice_detail";
    // Push通知設定
    private static final String FRAGMENT_PUSH_SETTING = "frag_push_setting";
    // アンケート登録
    private static final String FRAGMENT_QUESTION = "frag_question_setting";
    //license page
    private static final String FRAGMENT_LICENSE_PAGE = "frag_license_page";

    private FrameLayout mContentsView;
    private ModalWebView mModalWebView;
    private FrameLayout mContentsForPushView;

    private String mPushParamUrl = "";
    ServiceTopFragment fragment;

    //    画面サイズ用
    private Bundle bundle;
    private int desplay_width;
    private int desplay_height;

    private FirebaseAnalytics mFirebaseAnalytics;
    private int mScreenHeihgt;
    private static final int ANIM_DURATION = 300;
    private static final AppTopModel model = LMApplication.getApplication().getLmaSharedPreference().getAppTopJson();

    private LicenseFragment mLicenseFragment;
    private BaseFragment mCurrentFragment;
    @BindView(R.id.rg_tab1)
    RadioGroup mRadioGroup1;

    @BindView(R.id.rg_tab2)
    RadioGroup mRadioGroup2;

    @BindView(R.id.civ_home)
    ImageView mHomeImage;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_menu)
    ImageView mMenuIv;

    @BindView(R.id.iv_message)
    ImageView mMessageIv;

    @BindView(R.id.rl_menu)
    RelativeLayout mMenuRl;

    @BindView(R.id.iv_menu_close)
    ImageView mMenuCloseIv;

    @BindView(R.id.rv_menu1)
    RecyclerView mMenuRv1;

    @BindView(R.id.rv_menu2)
    RecyclerView mMenuRv2;

    @BindView(R.id.rv_menu3)
    RecyclerView mMenuRv3;

    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.iv_home_title)
    ImageView mHomeTitle;

    @BindView(R.id.iv_triangle)
    ImageView mTriangleIv;

    Badge mMessageBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ButterKnife.bind(this);


        // WindowManagerのインスタンス取得
        WindowManager wm = getWindowManager();

        // Displayのインスタンス取得
        Display disp = wm.getDefaultDisplay();

        // サイズ取得
        Point size = new Point();
        disp.getSize(size);
        desplay_width = size.x;
        desplay_height = size.y;

        mContentsView = (FrameLayout) findViewById(R.id.activity_home_contents_view);
        mContentsForPushView = (FrameLayout) findViewById(R.id.activity_home_push_contents_view);

        setFragmentMenu();

        mScreenHeihgt = ScreenUtil.getScreenHeight();
        initTabView();
        initMenuRv();
        initBadge();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTriangleIv.setBackground(buildBackground());
            mTriangleIv.setElevation(10.0f);
        }
    }

    private void initBadge() {
        mMessageBadge = new QBadgeView(this).bindTarget(mMessageIv);
    }

    private void setBadgeNumber(int number) {

        mMessageBadge.setBadgeNumber(number).stroke(getResources().getColor(R.color.toolbarBackgroundColor), 1, true);
        mMessageBadge.setShowShadow(false);
        if (number <= 99) {
            mMessageBadge.setBadgeTextSize(11, true)
                    .setBadgePadding(4, true)
                    .setGravityOffset(0, 0, true);
        } else {
            mMessageBadge.setBadgeTextSize(10, true)
                    .setBadgePadding(2, true)
                    .setGravityOffset(0, 2, true);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.FLAVOR.equals("product") || BuildConfig.FLAVOR.equals("staging")) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ServiceTop");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "App");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Start");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    /**
     * お知らせの未読件数を取得し、バッジをセットします。
     */
    private void setNoticeBadge() {

        final int maxListCount = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationCount();

        FunnelPush.getMessagesList(getApplicationContext(), new OnMessagesListApiListener() {
            @Override
            public void onSuccessGetMessagesList(MessagesListResponse messagesListResponse) {

                int noticeMessageCount = messagesListResponse.getMessageList().size();

                int unreadCounter = 0;

                if (maxListCount >= noticeMessageCount) {
                    //最大表示件数より少ない場合
                    for (int i = 0; i < noticeMessageCount; i++) {
                        if (!messagesListResponse.getMessageList().get(i).isRead()) {
                            //未読
                            unreadCounter = unreadCounter + 1;
                        }
                    }
                    //new ui start
                    setBadgeNumber(unreadCounter);
                    //new ui end
                } else {
                    //最大表示件数を超える場合
                    for (int i = 0; i < maxListCount; i++) {
                        if (!messagesListResponse.getMessageList().get(i).isRead()) {
                            //未読
                            unreadCounter = unreadCounter + 1;
                        }
                    }
                    //new ui start
                    setBadgeNumber(unreadCounter);
                    //new ui end
                }
            }

            @Override
            public void onFailGetMessagesList(String s, int i) {
                Log.d(getClass().getSimpleName(), s);

            }
        });


    }


    /**
     * メニュー画面をセットします.
     */
    private void setFragmentMenu() {
        Logger.d("top fragment load start");
        //サービストップのメニューから起動されたwebviewが表示されている場合は閉じる.
        if (mContentsView.getChildCount() > 0 || mContentsForPushView != null) {
            closeServicePage();
        }

        removePreFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (fragment != null && fragment.isAdded() && fragment.isVisible()) {
            return;
        }
        fragment = ServiceTopFragment.newInstance(mPushParamUrl, desplay_width, desplay_height);
        mCurrentFragment = fragment;
        ft.replace(R.id.activity_home_fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /**
     * お知らせ画面をセットします.
     */
    private void setFragmentNotice() {
        Logger.d("top fragment load start");
        //サービストップのメニューから起動されたwebviewが表示されている場合は閉じる.
        if (mContentsView.getChildCount() > 0 || mContentsForPushView != null) {
            closeServicePage();
        }

        String url = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationHref();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        NoticeFragment fragment = NoticeFragment.newInstance(url, false);
        mCurrentFragment = fragment;
        ft.replace(R.id.activity_home_fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setNoticeBadge();
    }

    /**
     * ご利用案内画面をセットします.
     */
    private void setFragmentTerms() {
        //サービストップのメニューから起動されたwebviewが表示されている場合は閉じる.
        if (mContentsView.getChildCount() > 0 || mContentsForPushView != null) {
            closeServicePage();
        }

        // 直前に表示されている画面がPush設定画面だった場合はstackされているのでremoveする.
        removePreFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        TermsFragment fragment = TermsFragment.newInstance();
        mCurrentFragment = fragment;
        ft.replace(R.id.activity_home_fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /**
     * 設定画面をセットします.
     */
    private void setFragmentSetting() {
        //サービストップのメニューから起動されたwebviewが表示されている場合は閉じる.
        if (mContentsView.getChildCount() > 0 || mContentsForPushView != null) {
            closeServicePage();
        }

        // 直前に表示されている画面がPush設定画面だった場合はstackされているのでremoveする.
        removePreFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SettingFragment fragment = SettingFragment.newInstance();
        mCurrentFragment = fragment;
        ft.replace(R.id.activity_home_fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void removePreFragment() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * サービストップのメニューから起動されたwebviewを閉じます.
     */
    private void closeServicePage() {
        if (mContentsView != null) {
            mContentsView.setVisibility(View.GONE);
        }
        if (mContentsForPushView != null) {
            mContentsForPushView.setVisibility(View.GONE);
        }
        if (mCurrentFragment != null && mCurrentFragment.isVisible()) {
            EventBusHolder.EVENT_BUS.post(new OnTitleChangeEvent(mCurrentFragment.getTitle()));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusHolder.EVENT_BUS.register(this);

        //Push通知で受信した遷移先URL
        mPushParamUrl = getIntent().getStringExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);
        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl);
        if (mPushParamUrl != null && !mPushParamUrl.isEmpty()) {
            //Push通知から起動された場合
            //push用のviewを表示する
            ModalWebView pushWebView = new ModalWebView(HomeActivity.this, mPushParamUrl, true, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW);
            mContentsForPushView.removeAllViews();
            mContentsForPushView.addView(pushWebView);
            mContentsForPushView.setVisibility(View.VISIBLE);
            mRadioGroup1.clearCheck();

            ImageButton backBtn = (ImageButton) pushWebView.findViewById(R.id.modal_action_bar_left_btn);
            backBtn.setVisibility(View.GONE);
            ImageButton closeBtn = (ImageButton) pushWebView.findViewById(R.id.modal_action_bar_right_btn);
            closeBtn.setVisibility(View.VISIBLE);
            closeBtn.setImageResource(R.mipmap.ic_clear_black_24dp_white);
            final ImageButton browserBackBtn = (ImageButton) pushWebView.findViewById(R.id.modal_action_bar_back_btn);
            final ImageButton browserForwardBtn = (ImageButton) pushWebView.findViewById(R.id.modal_action_bar_forward_btn);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContentsForPushView != null) {
                        mContentsForPushView.setVisibility(View.GONE);
                    }
                }
            });

            mContentsForPushView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            //< >ボタンの設定
            final WebView webView = (WebView) pushWebView.findViewById(R.id.modal_web_view);
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
            ImageView backIv = (ImageView) pushWebView.findViewById(R.id.iv_wv_back);
            ImageView forwardIv = (ImageView) pushWebView.findViewById(R.id.iv_wv_forward);
            ImageView closeIv = (ImageView) pushWebView.findViewById(R.id.iv_wv_close);
            backIv.setEnabled(false);
            forwardIv.setEnabled(false);

            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContentsForPushView != null) {
                        mContentsForPushView.setVisibility(View.GONE);
                    }
                    mRadioGroup1.check(R.id.rb_home);
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
            mPushParamUrl = "";
            getIntent().removeExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);
        }

        //お知らせ未読件数取得
        setNoticeBadge();

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
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(this.getClass().getSimpleName(), "pause executed");
        // ログイン実行しない、バックグラウンド状態になる
        SharedPrefsUtils.setBooleanPreference(this, PopUpAlertDialog.POPUP_LOGIN_START, false);
        EventBusHolder.EVENT_BUS.unregister(this);
    }

    /**
     * サービストップのwebview、お知らせ画面初期表示時にネットワークエラーとなった場合に画面を閉じます.
     *
     * @param event
     */
    @Subscribe
    public void onCalledViewCloseEvent(OnCalledViewCloseEvent event) {

        closeServicePage();
        if (OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW.equals(event.message) || OnCalledViewCloseEvent.SCREEN_HOME.equals(event.message)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.service_top_error_dialog_title)
                    .setMessage(R.string.service_top_error_dialog_message)
                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setFragmentMenu();
                            mRadioGroup1.clearCheck();
                        }
                    })
                    .show();
        } else if (OnCalledViewCloseEvent.SCREEN_SETTING.equals(event.message)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.net_error_title)
                    .setMessage(R.string.net_error_msg)
                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setFragmentSetting();
                        }
                    })
                    .show();

        } else if (OnCalledViewCloseEvent.SCREEN_TERMS.equals(event.message)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.net_error_title)
                    .setMessage(R.string.net_error_msg)
                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setFragmentTerms();
                        }
                    })
                    .show();

        }
    }

    /**
     * お知らせ画面の閉じるボタンがタップされたらホームへ遷移します。
     *
     * @param event
     */
    @Subscribe
    public void onNoticeCloseTapEvent(OnNoticeCloseTapEvent event) {
        setFragmentMenu();
    }

    /**
     * お知らせ詳細画面の閉じるボタンがタップされたらお知らせ一覧画面へ遷移します。
     *
     * @param event
     */
    @Subscribe
    public void onNoticeDetailCloseTap(OnNoticeDetailCloseTapEvent event) {
        setFragmentNotice();
    }

    @Subscribe
    public void OnSettingMenuCloseTap(OnSettingMenuCloseTapEvent event) {
        setFragmentSetting();
    }

    /**
     * お知らせ件数を再取得
     *
     * @param event
     */
    @Subscribe
    public void OnRefreshNoticeBadgeEvent(OnRefreshNoticeBadgeEvent event) {
        setNoticeBadge();
    }

    /**
     * お知らせ詳細画面をセットします。
     *
     * @param event
     */
    @Subscribe
    public void OnNoticeItemTapEvent(OnNoticeItemTapEvent event) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        NoticeDetailFragment fragment = NoticeDetailFragment.newInstance(event.messagesResponse);
        mCurrentFragment = fragment;
        ft.replace(R.id.activity_home_fragment, fragment, FRAGMENT_NOTICE_DETAIL);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setNoticeBadge();
    }

    /**
     * Push通知設定画面をセットします.
     *
     * @param event
     */
    @Subscribe
    public void onPushSettingTap(OnPushSettingTapEvent event) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PushSettingFragment fragment = PushSettingFragment.newInstance();
        ft.replace(R.id.activity_home_fragment, fragment, FRAGMENT_PUSH_SETTING);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /**
     * アンケート登録画面をセットします.
     *
     * @param event
     */
    @Subscribe
    public void onQuestionTap(OnQuestionTapEvent event) {

        if (event.message.equals(OnQuestionTapEvent.INIT)) {

            FunnelPush.getUserAttribute(getApplicationContext(), new OnUserAttributesApiListener() {
                @Override
                public void onSuccessGetUserAttributes(UserAttributes userAttributes) {
                    Log.d(getClass().getSimpleName(), userAttributes.getResult().getMessage());
                    Object a01 = null;
                    Object a02 = null;

                    if (userAttributes.getUserAttribute().getAppAttribute(getApplicationContext()) != null) {
                        a01 = userAttributes.getUserAttribute().getAppAttribute(getApplicationContext()).getAttrA01();
                        a02 = userAttributes.getUserAttribute().getAppAttribute(getApplicationContext()).getAttrA02();
                    }

                    String[] savedPrefectures = AccountUtils.getSavedQuestionPrefectures(a01);
                    String[] savedServices = AccountUtils.getSavedQuestionServices(a02);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    QuestionFragment fragment = QuestionFragment.newInstance(getString(R.string.user_info_ex_screen_settings), "", "", savedPrefectures, savedServices);
                    ft.replace(R.id.activity_home_fragment, fragment, FRAGMENT_QUESTION);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

                @Override
                public void onFailGetUser(String s, int i) {
                }
            });
        } else {
            setFragmentSetting();
        }
    }

    /**
     * SSOログイン失敗時にログイン画面へ遷移します.
     *
     * @param event
     */
    @Subscribe
    public void onSSOLoginFailed(OnSSOLoginFailedEvent event) {
        //Firebase イベントをレポートする
        Log.d(getClass().getSimpleName() + "/" + getLocalClassName(), event.error.getMessage());
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * @param event
     */
    @Subscribe
    public void onLicensePage(OnLicensePageEvent event) {
        if (event.isBack()) {
            setFragmentTerms();
        } else {
            if (mLicenseFragment != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_home_fragment, mLicenseFragment, FRAGMENT_LICENSE_PAGE)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            } else {
                mLicenseFragment = new LicenseFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_home_fragment, mLicenseFragment, FRAGMENT_LICENSE_PAGE)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
            mTitle.setText(mLicenseFragment.getTitle());
        }

    }

    /**
     * サービスメニュータップ時
     *
     * @param event
     */
    @Subscribe
    public void onMenuTapped(OnMenuTapEvent event) {
        // herfが「null または 空文字列」の場合終了
        if (event.url == null || event.url.length() == 0) {
            return;
        }

        checkOpenView(event);
    }

    @Subscribe
    public void onTitleChanged(OnTitleChangeEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getTitle())) {
            String title = event.getTitle();
            boolean isTop = title.equals("top");
            mTitle.setVisibility(isTop ? View.GONE : View.VISIBLE);
            mHomeTitle.setVisibility(isTop ? View.VISIBLE : View.GONE);
            if (title.length() > 10) {
                title = title.substring(0, 10) + "...";
            }
            mTitle.setText(title);
        }
    }

    /**
     * タブホアプリを開きます。
     *
     * @param event
     */
    private void showTabuhoApp(OnMenuTapEvent event) {

        String packageName = getString(R.string.app_unlimited_contents);

        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // 外部ブラウザ、WebView確認へ
            checkOpenView(event);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.app_unlimited_contents_uri)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(intent);
        }
    }

    /**
     * 外部ブラウザ、WebView　どちらで開くかを確認します。
     *
     * @param event
     */
    private void checkOpenView(OnMenuTapEvent event) {
        if (event.view.equals(getString(R.string.view_open_externalbrowser))) {
            showExternalBrowser(event.url);
        } else {
            if (event.isClearFragment && mCurrentFragment != null && !(mCurrentFragment instanceof ServiceTopFragment) && mCurrentFragment.isAdded() && mCurrentFragment.isVisible()) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(mCurrentFragment);
                transaction.commit();
                mTitle.setText("");
            }
            showWebView(event.url, event.sso, event.exScreen);
        }
    }

    /**
     * 外部ブラウザを表示します。
     *
     * @param url
     */
    private void showExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * WebViewを表示します。
     *
     * @param url
     * @param sso
     * @param exScreen
     */
    private void showWebView(String url, boolean sso, final String exScreen) {
        mRadioGroup1.clearCheck();
        if (mModalWebView != null) {
            mModalWebView.destoryWebView();
            mModalWebView = null;
        }
        mModalWebView = new ModalWebView(HomeActivity.this, url, sso, exScreen);
        mContentsView.removeAllViews();
        mContentsView.addView(mModalWebView);
        mContentsView.setVisibility(View.VISIBLE);

        if (mContentsForPushView != null) {
            mContentsForPushView.setVisibility(View.GONE);
        }

        Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + url);

        ImageButton backBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_left_btn);
        backBtn.setVisibility(View.GONE);
        ImageButton closeBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_right_btn);
        closeBtn.setVisibility(View.VISIBLE);
        closeBtn.setImageResource(R.mipmap.ic_clear_black_24dp_white);
        final ImageButton browserBackBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_back_btn);
        final ImageButton browserForwardBtn = (ImageButton) mModalWebView.findViewById(R.id.modal_action_bar_forward_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeServicePage();
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
                closeServicePage();
                if (OnCalledViewCloseEvent.SCREEN_HOME.equals(exScreen)) {
                    mRadioGroup1.check(R.id.rb_home);
                }
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
            if (mContentsForPushView.getVisibility() == View.VISIBLE) {
                // モーダル表示しているviewが表示中の場合
                WebView webView = (WebView) mContentsForPushView.findViewById(R.id.modal_web_view);
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if (mContentsForPushView != null) {
                        mContentsForPushView.setVisibility(View.GONE);
                    }
                }
            } else if (mContentsView.getVisibility() == View.VISIBLE) {
                // モーダル表示しているviewが表示中の場合
                WebView webView = (WebView) mModalWebView.findViewById(R.id.modal_web_view);
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    int checkedRadioButtonId = mRadioGroup1.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == R.id.rb_present || checkedRadioButtonId == R.id.rb_ticket) {
                        mRadioGroup1.check(R.id.rb_home);
                        updateHomeImage(R.id.rb_home);
                        return true;
                    } else {
                        closeServicePage();
                    }
                }
            } else {

                if (getFragmentManager().findFragmentByTag(FRAGMENT_PUSH_SETTING) != null && getFragmentManager().findFragmentByTag(FRAGMENT_PUSH_SETTING).isVisible() ||
                        getFragmentManager().findFragmentByTag(FRAGMENT_QUESTION) != null && getFragmentManager().findFragmentByTag(FRAGMENT_QUESTION).isVisible()) {
                    // プッシュ通知設定、アンケート画面表示中は設定画面を表示する
                    setFragmentSetting();
                } else if (getFragmentManager().findFragmentByTag(FRAGMENT_NOTICE_DETAIL) != null && getFragmentManager().findFragmentByTag(FRAGMENT_NOTICE_DETAIL).isVisible()) {
                    // お知らせ詳細表示中はお知らせ一覧を表示する
                    setFragmentNotice();
                } else if (getFragmentManager().findFragmentByTag(FRAGMENT_LICENSE_PAGE) != null && getFragmentManager().findFragmentByTag(FRAGMENT_LICENSE_PAGE).isVisible()) {
                    setFragmentTerms();
                } else {
                    // モーダル表示しているviewが表示中の場合
                    super.onKeyDown(keyCode, event);
                }

            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModalWebView != null) {
            mModalWebView = null;
        }
        if (mContentsView != null) {
            mContentsView.removeAllViews();
            mContentsView = null;
        }
    }


    @OnCheckedChanged({R.id.rb_present, R.id.rb_ticket, R.id.rb_home})
    public void onPresentChecked(CompoundButton button, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        updateHomeImage(button.getId());
        mMessageIv.setSelected(false);
        switch (button.getId()) {
            case R.id.rb_present:
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(true, model.present.href, model.present.sso, model.present.view, OnCalledViewCloseEvent.SCREEN_HOME));
                break;
            case R.id.rb_ticket:
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(true, model.ticketLucky.href, model.ticketLucky.sso, model.ticketLucky.view, OnCalledViewCloseEvent.SCREEN_HOME));
                break;
            case R.id.rb_home:
                setFragmentMenu();
                break;
        }
    }

    private void initTabView() {
        mRadioGroup1.check(R.id.rb_home);

    }

    @OnClick(R.id.rb_movie)
    public void onMovieClick() {
        EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.serviceVideo.href, model.serviceVideo.sso, model.serviceVideo.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
    }

    @OnClick(R.id.rb_bookes)
    public void onBooksClick() {
        EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.serviceMagazine.href, model.serviceMagazine.sso, model.serviceMagazine.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW, true));
    }

    private void updateHomeImage(int checkedId) {
        if (checkedId == R.id.rb_home) {
            mHomeImage.setImageResource(R.mipmap.ic_home_checked);
        } else {
            mHomeImage.setImageResource(R.mipmap.ic_home);
        }
    }

    @OnClick(R.id.iv_menu)
    public void onMenuClick(View v) {
        switchMenu();
    }


    @OnClick(R.id.iv_message)
    public void onMessageClick(View v) {
        mRadioGroup1.clearCheck();
        mRadioGroup2.clearCheck();
        updateHomeImage(R.id.iv_message);
        mMessageIv.setSelected(true);
        switchMenu(false);
        setFragmentNotice();
    }

    private void showMenu() {
        mMenuIv.setSelected(!mMenuIv.isSelected());
        mMenuRl.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -mScreenHeihgt, 0);
        translateAnimation.setDuration(ANIM_DURATION);
        translateAnimation.setFillAfter(true);
        mMenuRl.startAnimation(translateAnimation);

    }


    private void hideMenu() {
        mMenuIv.setSelected(!mMenuIv.isSelected());
        final TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -mScreenHeihgt);
        translateAnimation.setDuration(ANIM_DURATION);
        translateAnimation.setFillAfter(true);
        mMenuRl.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMenuRl.clearAnimation();
                mMenuRl.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @OnClick(R.id.iv_menu_close)
    public void onMenuCloseClick() {
        switchMenu();
    }

    private void switchMenu() {
        if (!mMenuIv.isSelected()) {
            showMenu();
        } else {
            hideMenu();
        }
    }

    private void switchMenu(boolean isShow) {
        if (isShow == mMenuIv.isSelected()) {
            //do nothing
            return;
        }
        if (isShow) {
            showMenu();
        } else {
            hideMenu();
        }
    }

    protected void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    private void initMenuRv() {
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager1.setAutoMeasureEnabled(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setAutoMeasureEnabled(true);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3.setAutoMeasureEnabled(true);

        List<MenuItem> items1 = new ArrayList<>();
        List<MenuItem> items2 = new ArrayList<>();
        List<MenuItem> items3 = new ArrayList<>();

        items1.add(new MenuItem(R.mipmap.ic_menu_ticket, model.ticketLucky, R.id.rb_ticket));
        items1.add(new MenuItem(R.mipmap.ic_menu_movie, model.serviceVideo));
        items1.add(new MenuItem(R.mipmap.ic_menu_books, model.serviceMagazine));

        items2.add(new MenuItem(R.mipmap.ic_menu_present, model.present, R.id.rb_present));
        items2.add(new MenuItem(R.mipmap.ic_menu_coupon, model.coupon));

        items3.add(new MenuItem(R.mipmap.ic_menu_config, model.coupon));
        items3.add(new MenuItem(R.mipmap.ic_menu_guide, model.coupon));
        items3.add(new MenuItem(R.mipmap.ic_menu_message, model.coupon));

        MenuAdapter adapter1 = new MenuAdapter(items1);
        MenuAdapter adapter2 = new MenuAdapter(items2);
        SettingAdapter adapter3 = new SettingAdapter(items3);
        mMenuRv1.setAdapter(adapter1);
        mMenuRv1.setLayoutManager(layoutManager1);
        mMenuRv2.setAdapter(adapter2);
        mMenuRv2.setLayoutManager(layoutManager2);
        mMenuRv3.setAdapter(adapter3);
        mMenuRv3.setLayoutManager(layoutManager3);

        adapter1.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {

            @Override
            public void onClick(MenuItem menuItem) {
                handleMenuClick(menuItem);
            }
        });
        adapter2.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {

            @Override
            public void onClick(MenuItem menuItem) {
                handleMenuClick(menuItem);

            }
        });
        adapter3.setOnItemClickListener(new SettingAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switchMenu();
                mMessageIv.setSelected(false);
                mRadioGroup1.clearCheck();
                mRadioGroup2.clearCheck();
                updateHomeImage(0);
                switch (position) {
                    case 0:
                        setFragmentSetting();
                        break;
                    case 1:
                        setFragmentTerms();
                        break;
                    case 2:
                        setFragmentNotice();
                        mMessageIv.setSelected(true);
                        break;
                }

            }
        });

    }

    private void handleMenuClick(MenuItem menuItem) {
        switchMenu();
        mMessageIv.setSelected(false);
        if (MenuItem.NO_TAB_INDEX == menuItem.getTabResId()) {
            mRadioGroup1.clearCheck();
            updateHomeImage(R.id.rb_home);
            EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(menuItem.getModel().href, menuItem.getModel().sso, menuItem.getModel().view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));

        } else {
            mRadioGroup1.check(menuItem.getTabResId());
        }
    }

    private Drawable buildBackground() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(getResources().getColor(R.color.toolbarBackgroundColor));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(buildConvexPath(), paint);
            }

            @Override
            public void getOutline(Outline outline) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    outline.setConvexPath(buildConvexPath());
                }
            }

            private Path buildConvexPath() {
                Path path = new Path();
                path.lineTo(rect().left, rect().top);
                path.lineTo(rect().right, rect().top);
                path.lineTo(rect().right / 2, rect().bottom / 2);

                path.close();
                return path;
            }
        });
    }

}
