package id.starkey.mitra.Firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Dani on 3/22/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TOKEN_BROADCAST = "myfcmtokenbroadcast";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("myfirebaseid", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        storeToken(refreshedToken);
        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
    }

    private void storeToken(String token){
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
