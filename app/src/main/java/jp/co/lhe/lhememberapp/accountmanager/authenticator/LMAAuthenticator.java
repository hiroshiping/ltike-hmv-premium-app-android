package jp.co.lhe.lhememberapp.accountmanager.authenticator;

import android.Manifest;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.activities.LoginActivity;

/**
 * Account Manager から呼ばれます.
 */

public class LMAAuthenticator extends AbstractAccountAuthenticator {

    public static final String LMA_ACCOUNT_TYPE = "jp.co.lhe.lhememberapp.accountmanager";
    public static final String LMA_AUTHTOKEN_TYPE = "webservice";
    public static final String LMA_AUTHTOKEN_LABEL = "LHE Member App Web Service";
    public static final String RE_AUTH_NAME = "reauth_name";

    protected final Context mContext;

    public LMAAuthenticator(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
        String authTokenType, String[] requiredFeatures, Bundle options)
        throws NetworkErrorException {
        AccountManager am = AccountManager.get(mContext);
        Account[] accounts = am.getAccountsByType(LMA_ACCOUNT_TYPE);
        Bundle bundle = new Bundle();

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS)
            != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        if (accounts.length > 0) {
            // 本サンプルコードではアカウントが既に存在する場合はエラーとする
            bundle.putString(AccountManager.KEY_ERROR_CODE, String.valueOf(-1));
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE,
                mContext.getString(R.string.error_account_exists));
        } else {
            // ★ポイント 2★ ログイン画面 Activity は Authenticator アプリで実装する
            // ★ポイント 4★ KEY_INTENT には、ログイン画面 Activity のクラス名を指定した明示的 Intent を与える
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
        Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
        String authTokenType, Bundle options) throws NetworkErrorException {
        Bundle bundle = new Bundle();
        if (accountExist(account)) {
            // ★ポイント 4★ KEY_INTENT には、ログイン画面 Activity のクラス名を指定した明示的 Intent を与える
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(RE_AUTH_NAME, account.name);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        } else {
            // 指定されたアカウントが存在しない場合はエラーとする
            bundle.putString(AccountManager.KEY_ERROR_CODE, String.valueOf(-2));
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE,
            mContext.getString(R.string.error_account_not_exists));
        }
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return LMA_AUTHTOKEN_LABEL;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
        String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
        String[] features) throws NetworkErrorException {
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    private boolean accountExist(Account account) {
        AccountManager am = AccountManager.get(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS)
            != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Account[] accounts = am.getAccountsByType(LMA_ACCOUNT_TYPE);
        for (Account ac : accounts) {
            if (ac.equals(account)) {
                return true;
            } }
        return false;
    }

}
