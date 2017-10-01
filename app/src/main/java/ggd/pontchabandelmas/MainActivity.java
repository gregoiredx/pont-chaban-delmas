package ggd.pontchabandelmas;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ggd.chaban.main";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        RequestQueue queue = Volley.newRequestQueue(this);
        PassagesRequest passagesRequest = new PassagesRequest(
                new Response.Listener<List<Passage>>() {
                    @Override
                    public void onResponse(List<Passage> passages) {
                        ArrayAdapter<Passage> adapter = new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_list_item_1, passages
                        );
                        listView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error getting passages", error);
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(error.getMessage())
                                .create().show();
                    }
                });
        queue.add(passagesRequest);
    }

    private static class PassagesRequest extends Request<List<Passage>> {

        private static final String URL = "https://data.bordeaux-metropole.fr/files.php?gid=489&format=6";
        private final Response.Listener<List<Passage>> listener;

        private PassagesRequest(Response.Listener<List<Passage>> listener, Response.ErrorListener errorListener) {
            super(Method.GET, URL, errorListener);
            this.listener = listener;
        }

        @Override
        protected Response<List<Passage>> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(new PrevisionsParser().parse(
                        new ByteArrayInputStream(response.data)),
                        HttpHeaderParser.parseCacheHeaders(response)
                );
            } catch (IOException e) {
                deliverError(new VolleyError("Could not read previsions", e));
            }
            return null;
        }

        @Override
        protected void deliverResponse(List<Passage> response) {
            listener.onResponse(response);
        }

    }
}
