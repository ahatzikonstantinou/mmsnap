package ahat.mmsnap.rest;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AuthStringRequest extends StringRequest
{
    /**
     * Creates a new request with the given method.
     *  @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public AuthStringRequest( int method, String url, Response.Listener<String> listener,
                              @Nullable JWTRefreshErrorListener errorListener )
    {
        super( method, url, listener, errorListener );
        errorListener.setParentRequest( this );
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + App.getJwtToken() );
        return headers;
    }

}
