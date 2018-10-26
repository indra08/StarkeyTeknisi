package id.starkey.mitra;

import android.os.Handler;

/**
 * Created by Dani on 4/12/2018.
 */

public class MyHandler {
    private static Handler handler;
    private static Runnable myRunnable;

    public static Handler getHandler() {
        if (handler == null) {
            initHandler();
        }
        return handler;
    }

    private static void initHandler() {
        handler = new Handler();
        handler.postDelayed(myRunnable,5000);
    }

    public static void stopMyHandler() {
        handler.removeCallbacksAndMessages(null);
    }

    public static void pauseMyHandler(Runnable myRunnable) {
        handler.removeCallbacksAndMessages(myRunnable);
    }

    public static void resumeMyHandler(Runnable myRunnable) {
        handler.postDelayed(myRunnable,5000);
    }

    public static void restartMyHandler(Runnable myRunnable){
        handler.postDelayed(myRunnable, 5000);
    }
}
