package com.techmax.shareforshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.techmax.shareforshare.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String PAYOUT_TYPE="com.techmax.shareforshare.PAYOUT_NOTIFICATION";
    private static final String REFER_TYPE="com.techmax.shareforshare.REFER_NOTIFICATION";
    private static final String REVIEW_TYPE="com.techmax.shareforshare.REVIEW_NOTIFICATION";
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "btc_app";
    private SharedPreferences pref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();
        String action = remoteMessage.getNotification().getClickAction();
        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if (getBoolean("notifications")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(getResources().getColor(R.color.colorAccent))
                            .setContentTitle(notification_title)
                            .setContentText(notification_body)
                            .setVibrate(new long[]{1000, 1000})
                            .setLights(Color.RED, 3000, 3000)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setAutoCancel(true);



            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), getString(R.string.notification_channel_name), mNotifyMgr.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mNotifyMgr.createNotificationChannel(channel);
            }


            if (action != null) {
                if (action.equals(PAYOUT_TYPE)) {
                    String txn_id = remoteMessage.getData().get("txn_id");
                    if (txn_id != null) {
                        Intent resultIntent = new Intent(action);
                        resultIntent.putExtra("txn_id", txn_id);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        mBuilder.setContentIntent(resultPendingIntent);

                        int mNotificationId = (int) System.currentTimeMillis();


                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }


                } else if (action.equals(REFER_TYPE)) {
                    String ui_url = remoteMessage.getData().get("ui_url");
                    if(ui_url!=null) {
                        mBuilder.setLargeIcon(getBitmapfromUrl(ui_url)).setStyle(new NotificationCompat.BigTextStyle());
                    }
                    Intent resultIntent = new Intent(action);

                    PendingIntent resultPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    mBuilder.setContentIntent(resultPendingIntent);

                    int mNotificationId = (int) System.currentTimeMillis();


                    mNotifyMgr.notify(mNotificationId, mBuilder.build());


                } else if (action.equals(REVIEW_TYPE)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //Try Google play
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));

                    PendingIntent resultPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    mBuilder.setContentIntent(resultPendingIntent);

                    int mNotificationId = (int) System.currentTimeMillis();


                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }

            } else {
                Intent resultIntent = new Intent(this, NotificationActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = (int) System.currentTimeMillis();


                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }


        }

    }
    public boolean getBoolean(String PREF_NAME) {
        return pref.getBoolean(PREF_NAME,true);
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
