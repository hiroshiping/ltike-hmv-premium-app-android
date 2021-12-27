package jp.co.lhe.lhememberapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ProgressBar;

import java.text.ParseException;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.utils.DateUtil;

public class BaseActivity extends Activity {

    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //onCreate時に操作時間を保存
        DateUtil.setLastOperationTime(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
            return true;
        }

        return false;
    }

    @Override
    protected void onPause() {
        //バックグラウンドへ行く前に最終操作時間を保存
        DateUtil.setLastOperationTime(this);

        Log.d(getClass().getSimpleName(), "****** onPause *******");

        super.onPause();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        Log.d(getClass().getSimpleName(), "****** onResume *******");

        super.onResume();
        long timeoutMinutes = ((LMApplication)getApplicationContext()).getLmaSharedPreference().getOperationTimeOutTime();
        long lastOperationTime = ((LMApplication)getApplicationContext()).getLmaSharedPreference().getLastOperationTime();
        if (timeoutMinutes == 0) {
            //何もしない
        } else {
            try {
                //一定時間操作がなかった場合はログイン画面へ遷移
                if (DateUtil.isTimeOver(timeoutMinutes,lastOperationTime)) {
                    //ログイン画面へ遷移
                    new AlertDialog.Builder(this)
                            .setTitle("確認")
                            .setMessage(getString(R.string.error_view_timeout))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    goSplash();
                                }
                            })
                            .show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void goSplash() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
