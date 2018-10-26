package id.starkey.mitra;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Dani on 6/8/2018.
 */

public class Apps extends Application {
    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fabric.with(this, new Crashlytics());
    }

    public static Context getmContext() {
        return mContext;
    }
}


