package id.starkey.mitra.Firebase;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import id.starkey.mitra.Kunci.OrderKunciActivity;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.NotificationOrder.NotificationOrder;
import id.starkey.mitra.NotificationOrder.NotificationOrderStempel;
import id.starkey.mitra.R;
import id.starkey.mitra.Stempel.OrderStempelActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dani on 3/22/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "fcmmessage";
    public static Activity mainAct;
    private JSONObject jsonObjectNotif;
    private String sTitleNotif, sBodyNotif;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

            String statusMessage = remoteMessage.getData().get("statusMessage");
            Log.d("statusMessageFire", statusMessage);
            if (statusMessage.equals("kunci_transaction_cancelled")){ //trx kunci cancel by user(customer) saat order sudah diterima mitra
                //Intent jumpMain = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                //jumpMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(jumpMain);
                OrderKunciActivity.getInstance().finish();
                String transaksiCancel = remoteMessage.getData().get("transaction");
                Log.d("payloadCancelUser", transaksiCancel);
            } else if(statusMessage.equals("kunci_transaction_search_cancelled")){// trx kunci cancel by user saat order belum diterima (layout masih dalam countdown)
                //close activity notificationOrder
                NotificationOrder.getInstance().finish();
                NotificationOrder.getInstance().myCountDownTimer.cancel();
                String trxCancelSearch = remoteMessage.getData().get("transaction");
                Log.d("cancelSearchByUser", trxCancelSearch);

            } else if (statusMessage.equals("stempel_transaction_cancelled")){ //trx kunci cancel by user(customer)
                OrderStempelActivity.getInstance().finish();

            } else if(statusMessage.equals("stempel_transaction_search_cancelled")){ //trx cancel by user saat order belum diterima (layout masih dalam countdown)
                //close activity notificationorderstempel
                NotificationOrderStempel.getInstance().finish();
                NotificationOrderStempel.getInstance().myCountDownTimer.cancel();
                String trxStempelCancelSearch = remoteMessage.getData().get("transaction");
                Log.d("StempelSearchByUser", trxStempelCancelSearch);
            } else if (statusMessage.equals("stempel_transaction_request")){ //trx stempel masuk
              Intent intent = new Intent(MyFirebaseMessagingService.this, NotificationOrderStempel.class);
              String trxStempel = remoteMessage.getData().get("transaction");
              String jenisTrx = remoteMessage.getData().get("jenisTransaksi");
              intent.putExtra("messageStempel", trxStempel);
              intent.putExtra("jenisTrxStempel", jenisTrx);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            } else { //trx kunci masuk
                //open activity
                Intent intent = new Intent(MyFirebaseMessagingService.this, NotificationOrder.class);
                //String pesan = remoteMessage.getNotification().getBody();
                //intent.putExtra("isNotification", true);
                // intent.putExtra("title", value1);
                String transaksiUser = remoteMessage.getData().get("transaction");
                String jenisTransaksi = remoteMessage.getData().get("jenisTransaksi");
                intent.putExtra("jenisTrx", jenisTransaksi);
                intent.putExtra("message", transaksiUser);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                //MainActivity.getInstance().finish();
                Log.d("payloadTransGetOrder", transaksiUser);
            }


        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
        try {
            String stringify = remoteMessage.getData().get("notification");
            jsonObjectNotif = new JSONObject(stringify);
            sTitleNotif = jsonObjectNotif.getString("title");
            sBodyNotif = jsonObjectNotif.getString("body");
            Log.d("bodynya", sBodyNotif);
        } catch (JSONException ex){

        }



        sendNotification(remoteMessage.getFrom(), sBodyNotif);
    }

    private void sendNotification (String from, String notification){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("keyID",idorder);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                // .setLargeIcon(image)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.mipmap.icon_starkey_mitra_curved)
                .setContentTitle("Starkey")
                .setContentText(notification)
                //     .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void notifyUser(String from, String notification){
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from, notification, new Intent(getApplicationContext(), MainActivity.class));
    }
}
