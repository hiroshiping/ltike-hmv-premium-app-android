package jp.co.lhe.lhememberapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.logger.Logger;

import java.util.Map;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.activities.HomeActivity;
import jp.co.lhe.lhememberapp.activities.SplashActivity;
import jp.funnelpush.sdk.FunnelPush;

public class FunnelPushForFcmService extends FirebaseMessagingService {

    private final static String TAG = "FunnelPushForFcmService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived");
        final Map<String, String> data = remoteMessage.getData();
        final RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (data.isEmpty()) {
            return;
        }

        //プッシュ通知設定で設定されている状態を取得
        boolean noticeEnabled = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationFlg();
        boolean soundEnabled = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationSoundFlg();
        boolean vibrationEnabled = ((LMApplication) getApplicationContext()).getLmaSharedPreference().getNotificationVibrationFlg();

        if (!noticeEnabled) {
            //配信を許可していない場合は表示しない
            return;
        }

        final String title = notification.getTitle();
        final String message = notification.getBody();
        final String messageId = data.get(FunnelPush.FUNNEL_PUSH_MESSAGE_ID);
        final String url = data.get(FunnelPush.FUNNEL_PUSH_MESSAGE_URL);

        //必要に応じて、Notificationを作成します。
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int notificationId = Integer.parseInt(messageId);
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId);
        notificationBuilder.setTicker(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        //プッシュ通知設定で設定されている状態を反映
        if (soundEnabled && !vibrationEnabled) {
            //サウンドのみON
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        } else if (!soundEnabled && vibrationEnabled) {
            //バイブレーションのみON
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        } else if (!soundEnabled && !vibrationEnabled) {
            //両方OFF
            notificationBuilder.setDefaults(0);
        } else {
            //両方ON
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        }

        //Pushタップ時の遷移先
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SplashActivity.class);

        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL, url);
        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_ID, messageId);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);


        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}
