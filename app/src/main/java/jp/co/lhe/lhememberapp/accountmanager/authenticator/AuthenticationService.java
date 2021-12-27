package jp.co.lhe.lhememberapp.accountmanager.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Authenticator のインスタンスを Account Manager に提供するサービス.
 */

public class AuthenticationService extends Service {

    private LMAAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new LMAAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
