package ggd.pontchabandelmas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String LOCAL_FILE = "previsions.csv";
    private static final String TAG = "ggd.pontchaban.main";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        Date startDate = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        if(isConnected()){
            initFromServer(startDate);
        }else {
            initFromLocalFile(startDate);
        }
    }

    private void initFromLocalFile(Date startDate) {
        try {
            FileInputStream fileInputStream = openFileInput(LOCAL_FILE);
            showPassages(new PrevisionsParser().parse(startDate, fileInputStream));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "No connectivity and no local file", e);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Pas de réseau et pas de sauvegarde locale")
                    .create().show();
        }catch (IOException | ParseException e) {
            Log.e(TAG, "Error getting passages from local file", e);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(e.getMessage())
                    .create().show();
        }
    }

    private void initFromServer(Date startDate) {
        RequestQueue queue = Volley.newRequestQueue(this);
        PassagesRequest passagesRequest = new PassagesRequest(
                this,
                startDate, new ResponseListener(listView),
                new ErrorListener());
        queue.add(passagesRequest);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private class PassagesAdapter extends BaseAdapter {

        private final List<Passage> passages;

        private PassagesAdapter(List<Passage> passages){
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
                convertView =  getLayoutInflater().inflate(R.layout.item, container, false);
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
            if(type.equals("Totale")) {
                typeView.setText("");
                typeView.setVisibility(View.GONE);
            }else {
                typeView.setText(type);
                typeView.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private  class ResponseListener implements Response.Listener<List<Passage>> {

        ResponseListener(ListView listView){
            MainActivity.this.listView = listView;
        }

        @Override
        public void onResponse(List<Passage> passages) {
            showPassages(passages);
        }
    }

    private void showPassages(List<Passage> passages) {
        listView.setAdapter(new PassagesAdapter(passages));
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error getting passages from server", error);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(error.getMessage())
                    .create().show();
        }
    }
}
