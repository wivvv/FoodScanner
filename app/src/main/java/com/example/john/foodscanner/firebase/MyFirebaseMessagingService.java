package com.example.john.foodscanner.firebase;

/**
 * Created by john on 8/31/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.john.foodscanner.Config;
import com.example.john.foodscanner.activities.LoginActivity;
import com.example.john.foodscanner.activities.MainActivity;
import com.example.john.foodscanner.utils.Get_CurrentDateTime;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.e(TAG, "From: " + remoteMessage.getFrom());

            if (remoteMessage == null)
                return;

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                handleNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        String datas = intent.getStringExtra("data");
        if (datas!=null){
            Log.e(TAG+": handleIntent", "Result: " + datas.toString());
            try{
                JSONObject jsonObject = new JSONObject(datas);
                String title = jsonObject.getString("title");
                final String message = jsonObject.getString("message");
                //boolean isBackground = data.getBoolean("is_background");
                String imageUrl = jsonObject.getString("image");
                String timestamp = new Get_CurrentDateTime().getCurrentDate()+","+new Get_CurrentDateTime().getCurrentTime();
                //JSONObject payload = data.getJSONObject("payload");
                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                //// Log.e(TAG, "isBackground: " + isBackground);
                ////Log.e(TAG, "payload: " + payload.toString());
                Log.e(TAG, "imageUrl: " + imageUrl);
                if (title.equals("share_all")){
                    String[] splits = message.split("/");
                    String body = splits[0];
                    try{
                        String name  = splits[1].split(":")[1];
                        String title_ = splits[1].split(":")[2];
                        String imei = name = splits[1].split(":")[3];

                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("message", body);
                        resultIntent.putExtra("name",name);
                        resultIntent.putExtra("title",title_);
                        resultIntent.putExtra("imei",imei);


                        //// check for image attachment
                        if (TextUtils.isEmpty(imageUrl)) {
                            showNotificationMessage(getApplicationContext(), splits[0], body, timestamp, resultIntent);
                            //new Custom(context).createNotify(splits[1],message);
                        } else {
                            // image is present, show notification with image
                            showNotificationMessageWithBigImage(getApplicationContext(), splits[0], body, timestamp, resultIntent, imageUrl);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {

                    String[] splits = title.split(":");
                    String head = splits[0];
                    String user = null;
                    String id = null;
                    if (splits.length>1){
                        user = splits[1];
                        id = splits[2];
                    }
                   if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        //// app is in foreground, broadcast the push message
                        // Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        //pushNotification.putExtra("message", message);
                        //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        //// play notification sound
                        //NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        // notificationUtils.playNotificationSound();

                        /** To be commented soon ****/

                                // play notification sound
                                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                                notificationUtils.playNotificationSound();


                                if (imageUrl != null || !imageUrl.equals("")){
                                    // new Custom(context).create_(splits[1],message,imageUrl);
                                }else {
                                    //new Custom(context).createNotify(splits[1],message);
                                }


                                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                                resultIntent.putExtra("message", message);
                                resultIntent.putExtra("id",splits[2]);
                                resultIntent.putExtra("name",splits[1]);
                                resultIntent.putExtra("category_id","category");

                                //// check for image attachment
                                if (TextUtils.isEmpty(imageUrl)) {
                                    showNotificationMessage(getApplicationContext(), splits[0], message, timestamp, resultIntent);
                                   // new Custom(context).createNotify(splits[1],message);
                                } else {

                                    // image is present, show notification with image
                                    showNotificationMessageWithBigImage(getApplicationContext(), splits[0], message, timestamp, resultIntent, imageUrl);
                                }


                            }


                   // } else {
                        // app is in background, show the notification in notification tray

                    }

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            Log.e(TAG+": handleIntent", "Empty " + datas);
        }
        // you can get ur data here
        //intent.getExtras().get("your_data_key")
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }
    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            final String message = data.getString("message");
            //boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = new Get_CurrentDateTime().getCurrentDate()+","+new Get_CurrentDateTime().getCurrentTime();
            //JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            //// Log.e(TAG, "isBackground: " + isBackground);
            ////Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            if (title.equals("share_all")){
                String[] splits = message.split("/");
                String body = splits[0];

                Toast.makeText(context,body,Toast.LENGTH_SHORT).show();

                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

                try{
                    String name  = splits[1].split(":")[1];
                    String title_ = splits[1].split(":")[2];
                    String imei = name = splits[1].split(":")[3];

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", body);
                    resultIntent.putExtra("name",name);
                    resultIntent.putExtra("title",title_);
                    resultIntent.putExtra("imei",imei);


                    //// check for image attachment
                     if (TextUtils.isEmpty(imageUrl)) {
                         showNotificationMessage(getApplicationContext(), splits[0], body, timestamp, resultIntent);
                        //new Custom(context).createNotify(splits[1],message);
                      } else {
                         // image is present, show notification with image
                         showNotificationMessageWithBigImage(getApplicationContext(), splits[0], body, timestamp, resultIntent, imageUrl);
                      }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }else {
                String[] splits = title.split(":");
                String head = splits[0];
                String user = null;
                String id = null;
                if (splits.length>1){
                    user = splits[1];
                    id = splits[2];
                }
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    //// app is in foreground, broadcast the push message
                   // Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    //pushNotification.putExtra("message", message);
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    //// play notification sound
                    //NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                   // notificationUtils.playNotificationSound();

                    /** To be commented soon ****/
                    if(!user.equals(null)){
                        if (user.equals("users")){

                            //// check for image attachment
                           // if (TextUtils.isEmpty(imageUrl)) {
                                //showNotificationMessage(getApplicationContext(), splits[0], message, timestamp, resultIntent);
                                //new Custom(context).createNotify(splits[1],message);
                            //} else {

                                // image is present, show notification with image
                               // showNotificationMessageWithBigImage(getApplicationContext(), splits[0], message, timestamp, resultIntent, imageUrl);
                            //}

                           // if (imageUrl != null || !imageUrl.equals("")){
                             //   new Custom(context).create_(splits[1],message,imageUrl);
                            //}else {
                                //new Custom(context).createNotify(splits[1],message);
                           // }

                        }else {

                            if (imageUrl != null || !imageUrl.equals("")){
                               // new Custom(context).create_(splits[1],message,imageUrl);
                            }else {
                                //new Custom(context).createNotify(splits[1],message);
                            }
                             //// check for image attachment
                           // if (TextUtils.isEmpty(imageUrl)) {
                                //showNotificationMessage(getApplicationContext(), splits[0], message, timestamp, resultIntent);
                                //new Custom(context).createNotify(splits[1],message);
                            //} else {
                                // image is present, show notification with image
                                //showNotificationMessageWithBigImage(getApplicationContext(), splits[0], message, timestamp, resultIntent, imageUrl);
                            //}
                        }
                    }

                } else {
                    // app is in background, show the notification in notification tray

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * ////Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}