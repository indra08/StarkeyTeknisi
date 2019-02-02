package id.starkey.mitra.Firebase;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.Kunci.OrderKunciActivity;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.NotificationOrder.NotificationOrder;
import id.starkey.mitra.NotificationOrder.NotificationOrderStempel;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Stempel.OrderStempelActivity;
import id.starkey.mitra.Utilities.StatusMitra;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dani on 3/22/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "fcmmessage";
    public static Activity mainAct;
    private JSONObject jsonObjectNotif;
    private String sTitleNotif, sBodyNotif;
    private final int TimertCount = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        boolean shouldNotifAppear = true;
        // ...
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.

        Intent intent = new Intent(this, MainActivity.class);

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
                try {
                    OrderKunciActivity.getInstance().finish();
                    String transaksiCancel = remoteMessage.getData().get("transaction");
                    Log.d("payloadCancelUser", transaksiCancel);
                }catch (Exception e){e.printStackTrace();}

            } else if(statusMessage.equals("kunci_transaction_search_cancelled")){// trx kunci cancel by user saat order belum diterima (layout masih dalam countdown)
                //close activity notificationOrder
                try {

                    NotificationOrder.getInstance().finish();
                    NotificationOrder.getInstance().myCountDownTimer.cancel();
                    String trxCancelSearch = remoteMessage.getData().get("transaction");
                    Log.d("cancelSearchByUser", trxCancelSearch);
                }catch (Exception e){e.printStackTrace();}

            } else if (statusMessage.equals("stempel_transaction_cancelled")){ //trx kunci cancel by user(customer)

                try {
                    OrderStempelActivity.getInstance().finish();
                }catch (Exception e){e.printStackTrace();}


            } else if(statusMessage.equals("stempel_transaction_search_cancelled")){ //trx cancel by user saat order belum diterima (layout masih dalam countdown)
                //close activity notificationorderstempel

                try {

                    NotificationOrderStempel.getInstance().finish();
                    NotificationOrderStempel.getInstance().myCountDownTimer.cancel();
                    String trxStempelCancelSearch = remoteMessage.getData().get("transaction");
                    Log.d("StempelSearchByUser", trxStempelCancelSearch);
                }catch (Exception e){e.printStackTrace();}

            } else if (statusMessage.equals("stempel_transaction_request")){ //trx stempel masuk

                if(StatusMitra.status == 2){ // masih ada pesanan

                    shouldNotifAppear = false;
                    mitraDeclinedOrderStempel(remoteMessage.getData().get("transaction"));
                }else{

                    //Intent intent = new Intent(MyFirebaseMessagingService.this, NotificationOrderStempel.class);
                    intent = new Intent(MyFirebaseMessagingService.this, NotificationOrderStempel.class);
                    String trxStempel = remoteMessage.getData().get("transaction");
                    String jenisTrx = remoteMessage.getData().get("jenisTransaksi");
                    intent.putExtra("messageStempel", trxStempel);
                    intent.putExtra("jenisTrxStempel", jenisTrx);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else { //trx kunci masuk

                if(StatusMitra.status == 2){ // masih ada pesanan

                    shouldNotifAppear = false;
                    mitraDeclinedOrderKunci(remoteMessage.getData().get("transaction"));
                }else{

                    //open activity
                    //Intent intent = new Intent(MyFirebaseMessagingService.this, NotificationOrder.class);
                    intent = new Intent(MyFirebaseMessagingService.this, NotificationOrder.class);
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

        if (shouldNotifAppear) sendNotification(remoteMessage.getFrom(), sBodyNotif, intent);
    }

    private void sendNotification(String from, String notification, Intent intent){
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

    //webservice pendukung
    private void mitraDeclinedOrderKunci(String pesan){

        String id = "";
        try {
            JSONObject joOrderMasuk = new JSONObject(pesan);
            id = joOrderMasuk.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", id);
        params.put("declined", "false");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resp = response.toString();
                        Log.d("mitDec", resp);
                        /*
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                         */

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
                String tokennyaUser = custDetails.getString("tokenIdUser", "");

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void mitraDeclinedOrderStempel(String pesan){
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject joOrderStempel = null;
        String id = "";
        try {
            joOrderStempel = new JSONObject(pesan);
            id = joOrderStempel.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put("id_transaksi_user", id);
        params.put("declined", "false");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resp = response.toString();
                        Log.d("mitDec", resp);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
                String tokennyaUser = custDetails.getString("tokenIdUser", "");
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }
}
