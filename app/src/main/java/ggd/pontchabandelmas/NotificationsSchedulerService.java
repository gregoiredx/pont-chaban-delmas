package ggd.pontchabandelmas;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


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
        try {
            scheduleNotifications(new LocalPassages(this).read(), activateNotifications);
        } catch (IOException | ParseException e) {
            toast(R.string.read_passages_error_while_scheduling_notifications);
        }
    }

    private void scheduleNotifications(List<Passage> passages, boolean activateNotifications) {
        Intent startNotificationIntent = new Intent(this, NotificationAlarmReceiver.class);
        startNotificationIntent.putExtra(NotificationAlarmReceiver.NOTIFICATION_TEXT, "passage " + passages.get(0).closing);
        Log.i("ggd.pontchaban.sched", "scheduling");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, startNotificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, new Date().getTime() + 3000, pendingIntent);

        toast(activateNotifications ? R.string.notifications_activated : R.string.notifications_deactivated);
    }

    private void toast(final int resId) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
