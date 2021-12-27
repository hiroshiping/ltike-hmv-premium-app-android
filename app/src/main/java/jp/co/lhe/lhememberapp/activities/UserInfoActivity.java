package jp.co.lhe.lhememberapp.activities;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.logger.Logger;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.fragments.QuestionFragment;
import jp.co.lhe.lhememberapp.network.models.ToastInfo;
import jp.co.lhe.lhememberapp.ui.utils.AccountUtils;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnUserAttributesApiListener;
import jp.funnelpush.sdk.response.UserAttributes;

/**
 * Activity
 * 01-06.起動_アンケート画面
 */
public class UserInfoActivity extends BaseActivity implements QuestionFragment.OnFragmentInteractionListener  {

    private String exScreen;
    private String url;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String exScreen = getIntent().getStringExtra(getString(R.string.user_info_param));
            if (getString(R.string.user_info_ex_screen_settings).equals(exScreen)) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        exScreen = getIntent().getStringExtra(getString(R.string.user_info_param));
        url = getIntent().getStringExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);

        init();
    }

    private void init() {
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
                QuestionFragment fragment = QuestionFragment.newInstance(exScreen, "", url, savedPrefectures, savedServices);
                ft.replace(R.id.activity_user_info_fragment, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            @Override
            public void onFailGetUser(String s, int i) {
                Logger.d(s);
                //Firebase イベントをレポートする
                String errContent = s+ "、code:"+ i;
                String nextAction = FirebaseUtil.getActionDialog(ToastInfo.getNetErrorInfo().getTitle(),
                        ToastInfo.getNetErrorInfo().getMessage());
                FirebaseUtil.logEventFunnelPushData(UserInfoActivity.this, getLocalClassName(),  errContent, nextAction);
                showNetToast(ToastInfo.getNetErrorInfo(),true);
            }
        });
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
                        init();
                    }
                })
                .show();
        return;
    }



}
