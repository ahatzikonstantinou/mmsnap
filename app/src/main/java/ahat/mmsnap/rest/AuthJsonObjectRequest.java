package ahat.mmsnap.rest;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthJsonObjectRequest extends JsonObjectRequest
{
    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null indicates no
     *                      parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public AuthJsonObjectRequest( int method, String url, @Nullable JSONObject jsonRequest,
                                  Response.Listener<JSONObject> listener,
                                  @Nullable Response.ErrorListener errorListener )
    {
        super( method, url, jsonRequest, listener, errorListener );
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + App.getJwtToken() );
        return headers;
    }
}
