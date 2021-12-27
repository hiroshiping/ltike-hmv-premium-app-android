package jp.co.lhe.lhememberapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnUniqueCodeApiListener;
import jp.funnelpush.sdk.callback.OnUserIdChangedListener;
import jp.funnelpush.sdk.response.UsersResponse;

public class LogoutService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FunnelPush.unlinkUniqueCode((LMApplication) getApplicationContext(), new OnUserIdChangedListener() {
            @Override
            public void onUserIdChanged(String s) {
                Log.d(getClass().getSimpleName(), "[Unlink]ユーザーIDが変更されました。:" + s);
            }
        }, new OnUniqueCodeApiListener() {
            @Override
            public void onSuccessSendUniqueCode(UsersResponse usersResponse) {
                Logger.d(usersResponse);
            }

            @Override
            public void onFailSendUniqueCode(String s, int i) {
                Logger.d(s);
            }
        });

    }
}
