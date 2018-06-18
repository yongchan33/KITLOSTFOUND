package kr.ac.kumoh.LostNFound;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    //private final static String TAG = "FCM_MESSAGE";
    private final static String MYCHANNEL = "KIT LOST&FOUND";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            //Log.d(TAG, "Notification Body: " + body);

            PendingIntent mPendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this, 0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), MYCHANNEL)
                    .setSmallIcon(R.drawable.appinstall) // 푸시 알림 영역에 노출 될 아이콘.   //R.mipmap.ic_launcher 기본 값
                    .setContentTitle("금오공대 Lost&Found") // 푸시 알림 영역에 노출 될 타이틀
                    .setContentText(body) // Firebase Console 에서 사용자가 전달한 메시지내용
                    .setContentIntent(mPendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0x1001, notificationBuilder.build());
        }
    }
}