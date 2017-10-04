package ggd.pontchabandelmas;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;


public class NotificationsSchedulerService extends IntentService {

    public static final String ACTIVATE_NOTIFICATIONS_EXTRA = "activateNotifications";

    public NotificationsSchedulerService() {
        super("NotificationsSchedulerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null || ! intent.hasExtra(ACTIVATE_NOTIFICATIONS_EXTRA)){
            return;
        }
        final boolean activateNotifications = intent.getBooleanExtra(ACTIVATE_NOTIFICATIONS_EXTRA, false);


        Intent startNotificationIntent = new Intent(this, NotificationAlarmReceiver.class);
        startNotificationIntent.putExtra(NotificationAlarmReceiver.NOTIFICATION_TEXT, "a text received later");
        Log.i("ggd.pontchaban.sched", "scheduling");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, startNotificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, new Date().getTime() + 3000, pendingIntent);

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), activateNotifications ?  R.string.notifications_activated : R.string.notifications_deactivated, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
