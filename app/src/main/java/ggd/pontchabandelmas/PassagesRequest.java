package ggd.pontchabandelmas;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

class PassagesRequest extends Request<List<Passage>> {

    private static final String URL = "https://data.bordeaux-metropole.fr/files.php?gid=489&format=6";
    private static final String TAG = "ggd.pontchaban.req";
    private final Context context;
    private final Response.Listener<List<Passage>> listener;
    private final Date startDate;

    PassagesRequest(Context context, Date startDate, Response.Listener<List<Passage>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, errorListener);
        this.context = context;
        this.listener = listener;
        this.startDate = startDate;
    }

    @Override
    protected Response<List<Passage>> parseNetworkResponse(NetworkResponse response) {
        try {
            writeLocalFile(response.data);
            return Response.success(new PrevisionsParser().parse(startDate,
                    new ByteArrayInputStream(response.data)),
                    HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (IOException | ParseException e) {
            deliverError(new VolleyError("Could not read previsions", e));
        }
        return null;
    }

    private void writeLocalFile(byte[] data) throws IOException {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(MainActivity.LOCAL_FILE, Context.MODE_PRIVATE);
            fileOutputStream.write(data);
            fileOutputStream.close();
        }catch (IOException e){
            Log.e(TAG, "Could not write local file", e);
        }
    }

    @Override
    protected void deliverResponse(List<Passage> response) {
        listener.onResponse(response);
    }

}
