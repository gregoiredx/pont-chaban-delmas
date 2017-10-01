package ggd.pontchabandelmas;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ggd.pontchaban.main";
    private static final SimpleDateFormat CLOSING_DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM yyyy 'de' HH:mm", Locale.FRANCE);
    private static final SimpleDateFormat REOPENING_DATE_FORMAT = new SimpleDateFormat(" 'à' HH:mm", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listView);
        RequestQueue queue = Volley.newRequestQueue(this);
        PassagesRequest passagesRequest = new PassagesRequest(
                new ResponseListener(listView),
                new ErrorListener());
        queue.add(passagesRequest);
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
            ((TextView) convertView.findViewById(R.id.closing)).setText(CLOSING_DATE_FORMAT.format(getItem(position).closing));
            ((TextView) convertView.findViewById(R.id.reopening)).setText(REOPENING_DATE_FORMAT.format(getItem(position).reopening));
            ((TextView) convertView.findViewById(R.id.boat)).setText(getItem(position).boat);
            ((TextView) convertView.findViewById(R.id.type)).setText(getItem(position).type);
            return convertView;
        }
    }

    private  class ResponseListener implements Response.Listener<List<Passage>> {

        private ListView listView;

        ResponseListener(ListView listView){
            this.listView = listView;
        }

        @Override
        public void onResponse(List<Passage> passages) {
            listView.setAdapter(new PassagesAdapter(passages));
        }
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error getting passages", error);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(error.getMessage())
                    .create().show();
        }
    }
}
