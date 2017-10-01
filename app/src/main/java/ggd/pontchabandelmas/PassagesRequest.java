package ggd.pontchabandelmas;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

class PassagesRequest extends Request<List<Passage>> {

    private static final String URL = "https://data.bordeaux-metropole.fr/files.php?gid=489&format=6";
    private final Response.Listener<List<Passage>> listener;

    PassagesRequest(Response.Listener<List<Passage>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<List<Passage>> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new PrevisionsParser().parse(new Date(new Date().getTime() - 1000 * 60 * 60 * 24),
                    new ByteArrayInputStream(response.data)),
                    HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (IOException | ParseException e) {
            deliverError(new VolleyError("Could not read previsions", e));
        }
        return null;
    }

    @Override
    protected void deliverResponse(List<Passage> response) {
        listener.onResponse(response);
    }

}
