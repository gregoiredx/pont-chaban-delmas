package ggd.pontchabandelmas;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.io.FileOutputStream;
import java.io.IOException;

public class UpdatePassagesService extends IntentService {

    public static final String PASSAGES_UPDATE_RESULT_INTENT_ACTION = "passages-updated";
    public static final String SUCCESSFUL_EXTRA = "sucessfull";
    private static final String TAG = "ggd.pontchaban.update";
    private static final String URL = "https://data.bordeaux-metropole.fr/files.php?gid=489&format=6";

    public UpdatePassagesService() {
        super("UpdatePassagesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(isConnected()) {
            Volley.newRequestQueue(this).add((Request) new UpdateLocalFileRequest());
        }else {
            sendUpdateResultIntent(false);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private class UpdateLocalFileRequest extends Request<byte[]> {

        private UpdateLocalFileRequest() {
            super(Method.GET, URL, new ErrorListener());
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(byte[] response) {
            try {
                FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(MainActivity.LOCAL_FILE, Context.MODE_PRIVATE);
                fileOutputStream.write(response);
                fileOutputStream.close();
                sendUpdateResultIntent(true);
            } catch (IOException e) {
                getErrorListener().onErrorResponse(new VolleyError("Could not write local file", e));
            }

        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error getting passages from server", error);
            sendUpdateResultIntent(false);
        }

    }

    private void sendUpdateResultIntent(boolean successful) {
        Intent intent = new Intent(PASSAGES_UPDATE_RESULT_INTENT_ACTION);
        intent.putExtra(SUCCESSFUL_EXTRA, successful);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i(TAG, "update passages result sent: successful " + successful);
    }

}
