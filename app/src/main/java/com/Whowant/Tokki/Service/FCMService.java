package com.Whowant.Tokki.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Response;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.

        SharedPreferences pref = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        boolean bAlarm = pref.getBoolean("ALARM_SETTING", true);
        boolean bSound = pref.getBoolean("SOUND_SETTING", true);

   //     if(!bAlarm)
    //        return;

      //  if(pref.getString("USER_ID", "").length() == 0) {
      //      return;
       // }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent introIntent = new Intent(getApplicationContext(), MainActivity.class);

//        HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("PanBook", "PanBook", importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "PanBook");
        } else {
            builder = new Notification.Builder(this);
        }


    //    if(remoteMessage.getData().get("TYPE") == null)
    //        return;

    //    String objectId = remoteMessage.getData().get("Bundle").get("gcm.notification.OBJECT_ID");

        ;


        RemoteMessage.Notification  _type =  remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        String type =  data.get("TYPE").toString();

        String objectId =  data.get("OBJECT_ID").toString();



        int nType = Integer.valueOf(type);
        int nObjectID = Integer.valueOf(objectId);

        introIntent.putExtra("TYPE", nType);
        introIntent.putExtra("OBJECT_ID", nObjectID);


     //   introIntent.putExtra("TYPE", 33);
     //   introIntent.putExtra("OBJECT_ID", 163);
        introIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, introIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.ic_stat_name) // 아이콘 설정하지 않으면 오류남
                .setContentTitle(remoteMessage.getData().get("title")) // 제목 설정
                .setContentText(remoteMessage.getData().get("body")) // 내용 설정
                .setTicker(remoteMessage.getData().get("body")) // 상태바에 표시될 한줄 출력
                .setAutoCancel(true)
                .setContentIntent(intent);

        if(bSound)
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        else
            builder.setDefaults(Notification.DEFAULT_VIBRATE);

        notificationManager.notify((int)System.currentTimeMillis(), builder.build());
    }


    static public String g_Token = "";
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        g_Token = token;
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}