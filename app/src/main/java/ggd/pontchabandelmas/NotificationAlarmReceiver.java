package ggd.pontchabandelmas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import static android.os.Looper.getMainLooper;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_TEXT = "notification_text";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent == null || ! intent.hasExtra(NOTIFICATION_TEXT)){
            return;
        }
        final String notificationText = intent.getStringExtra(NOTIFICATION_TEXT);
        Log.i("ggd.pontchaban.receiv", notificationText);
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), notificationText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
