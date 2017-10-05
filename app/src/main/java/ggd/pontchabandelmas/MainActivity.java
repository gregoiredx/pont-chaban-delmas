package ggd.pontchabandelmas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String LOCAL_FILE = "previsions.csv";
    private static final String TAG = "ggd.pontchaban.main";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    private ListView listView;

    private BroadcastReceiver passagesUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean successful = intent.getBooleanExtra(UpdatePassagesService.SUCCESSFUL_EXTRA, false);
            Log.i(TAG, "received update passages result: successful " + successful);
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), successful ? R.string.passages_update_success : R.string.passages_update_failed, Toast.LENGTH_SHORT).show();
                }
            });
            initFromLocalFile();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        LocalBroadcastManager.getInstance(this).registerReceiver(passagesUpdateReceiver, new IntentFilter(UpdatePassagesService.PASSAGES_UPDATE_RESULT_INTENT_ACTION));
        startService(new Intent(this, UpdatePassagesService.class));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(passagesUpdateReceiver);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean notificationsActive = sharedPref.getBoolean("notifications_active_pref", false);
        MenuItem item = menu.findItem(R.id.notifications_toggle);
        item.setIcon(notificationsActive ? R.drawable.ic_notifications_on : R.drawable.ic_notifications_off);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications_toggle:
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                boolean notificationsActive = sharedPref.getBoolean("notifications_active_pref", false);
                boolean newNotificationsActivationState = !notificationsActive;
                Intent startNotificationSchedulerIntent = new Intent(this, NotificationsSchedulerService.class);
                startNotificationSchedulerIntent.putExtra(NotificationsSchedulerService.ACTIVATE_NOTIFICATIONS_EXTRA, newNotificationsActivationState);
                startService(startNotificationSchedulerIntent);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("notifications_active_pref", newNotificationsActivationState);
                editor.apply();
                item.setIcon(newNotificationsActivationState ? R.drawable.ic_notifications_on : R.drawable.ic_notifications_off);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFromLocalFile() {
        try {
            List<Passage> passages = Passages.read(this);
            listView.setAdapter(new PassagesAdapter(passages));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "No local file", e);
            new AlertDialog.Builder(MainActivity.this).setMessage(R.string.no_local_file).create().show();
        } catch (IOException | ParseException e) {
            Log.e(TAG, "Error getting passages from local file", e);
            new AlertDialog.Builder(MainActivity.this).setMessage(R.string.invalid_local_file).create().show();
        }
    }

    private class PassagesAdapter extends BaseAdapter {

        private final List<Passage> passages;

        private PassagesAdapter(List<Passage> passages) {
            this.passages = passages;
        }

        @Override
        public int getCount() {
            return passages.size();
        }

        @Override
        public Passage getItem(int i) {
            return passages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return passages.get(i).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item, container, false);
            }
            Passage item = getItem(position);
            ((TextView) convertView.findViewById(R.id.date)).setText(
                    getString(R.string.date, DATE_FORMAT.format(item.closing))
            );
            ((TextView) convertView.findViewById(R.id.hours)).setText(
                    getString(R.string.time, TIME_FORMAT.format(item.closing), TIME_FORMAT.format(item.reopening)
                    ));
            ((TextView) convertView.findViewById(R.id.boat)).setText(item.boat);
            String type = item.type;
            TextView typeView = convertView.findViewById(R.id.type);
            if (type.equals("Totale")) {
                typeView.setText("");
                typeView.setVisibility(View.GONE);
            } else {
                typeView.setText(type);
                typeView.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }


}
