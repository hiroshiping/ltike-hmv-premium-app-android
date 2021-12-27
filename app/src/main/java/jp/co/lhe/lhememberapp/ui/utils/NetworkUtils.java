package jp.co.lhe.lhememberapp.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    /**
     * ネットワーク接続チェック
     * 起動時にオンライン状態か確認する
     * @return true:OK/false:NG
     */
    public static boolean chkOnline(Context context) {
        // インターネットに接続していない場合は「電波の良い所で試してください」のようなメッセージを表示する
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }
        return true;
    }

}
