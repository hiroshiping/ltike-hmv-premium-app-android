package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.activities.LoginActivity;
import jp.co.lhe.lhememberapp.network.models.AppSettingsModel;
import jp.co.lhe.lhememberapp.service.LogoutService;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnPushSettingTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnQuestionTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.utils.NetworkUtils;

/**
 * 設定Fragment
 */
public class SettingFragment extends BaseFragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //アンケート
        RelativeLayout userInfoBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_setting_user_info_block);
        userInfoBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnQuestionTapEvent(OnQuestionTapEvent.INIT));
                if (!NetworkUtils.chkOnline(getActivity())) {
                    EventBusHolder.EVENT_BUS.post( new OnCalledViewCloseEvent(OnCalledViewCloseEvent.SCREEN_SETTING));
                }
            }
        });

        final AppSettingsModel model = ((LMApplication) getActivity().getApplicationContext()).getLmaSharedPreference().getChangeSettings();

        //サービス登録情報変更
        RelativeLayout settingsBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_setting_settings_block);
        settingsBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.changeSettings.href, model.changeSettings.sso, OnCalledViewCloseEvent.SCREEN_SETTING));
            }
        });

        //ローチケマイページ
        RelativeLayout mypageBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_setting_mypage_block);
        mypageBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.mypage.href, model.mypage.sso, OnCalledViewCloseEvent.SCREEN_SETTING));
            }
        });


        //Push通知設定
        RelativeLayout pushBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_setting_push_block);
        pushBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnPushSettingTapEvent(""));
            }
        });

        //ログアウト
        RelativeLayout logoutBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_setting_logout_block);
        logoutBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                .setTitle("確認")
                .setMessage("ログアウトします。よろしいですか？")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    startLogoutService();
                    //SharedPreferenceからアカウント情報を削除する。
                    ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().removeAccountInfo();
                    //保存されている情報(有料区分、契約コース)を削除
                    ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().removeLoginAccountInfo();

                    //cookieの削除
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CookieManager.getInstance().removeAllCookies(null);
                        CookieManager.getInstance().flush();
                    } else {
                        CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getActivity().getApplicationContext());
                        cookieSyncMngr.startSync();
                        CookieManager cookieManager=CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        cookieManager.removeSessionCookie();
                        cookieSyncMngr.stopSync();
                        cookieSyncMngr.sync();
                    }
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        }
                })
                .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

            }
        });

        return view;

    }

    private void startLogoutService() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), LogoutService.class);
        getActivity().startService(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getTitle() {
        return getString(R.string.setting_title);
    }
}
