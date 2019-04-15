package ahat.mmsnap.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.R;

public class JWTRefreshErrorListener implements Response.ErrorListener
{
    public interface Listener
    {
        void onError( VolleyError error );
    }

    private Context context;
    private Request parentRequest;
    public void setParentRequest( Request request ){ parentRequest = request; }
    private Listener listener;
    private boolean jwtRefreshAlreadyAttempted;
    private String currentPassword;

    public JWTRefreshErrorListener( Context context, Listener listener )
    {
        this.context = context;
        this.parentRequest = null;
        this.listener = listener;
        jwtRefreshAlreadyAttempted = false;
        currentPassword = null;
    }

    public JWTRefreshErrorListener( Context context, String currentPassword, Listener listener )
    {
        this.context = context;
        this.parentRequest = null;
        this.listener = listener;
        jwtRefreshAlreadyAttempted = false;
        this.currentPassword = currentPassword;
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse( final VolleyError error )
    {
        if( !jwtRefreshAlreadyAttempted && 401 == error.networkResponse.statusCode && null != parentRequest )
        {
            jwtRefreshAlreadyAttempted = true;
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
            final RequestQueue queue = Volley.newRequestQueue( context );
            String url = RESTService.REST_URL + "/api/authenticate";
            JSONObject jsonBody = new JSONObject();
            if( null == currentPassword )
            {
                currentPassword = settings.getString( context.getString( R.string.key_password ), "" );
            }
            try
            {
                jsonBody.put( "username", settings.getString( context.getString( R.string.key_username ), "" ) );
                jsonBody.put( "password", currentPassword );
                jsonBody.put( "rememberMe", true );
            }
            catch( JSONException e )
            {
                e.printStackTrace();
                Log.e( "MMSNAP:", "Error during jwt refresh: " + e.getMessage() );
            }

            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse( JSONObject response )
                    {
                        // success!
                        // refresh jwt
                        try
                        {
                            App.setJwtToken( response.getString( "id_token" ) );
                            queue.add( parentRequest );
                        }
                        catch ( JSONException e )
                        {
                            e.printStackTrace();
                            Log.e( "MMSNAP:", "Error receiving JWT refresh answer: " + e.getMessage() );
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse( VolleyError error)
                    {
                        String message = "JWT refresh failed. Error: " + error.getMessage();
                        Log.e( "MMSNAP:", message );
//                        Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
                        listener.onError( error );
                    }
                });
            queue.add(request);
        }
        else
        {
            listener.onError( error );
        }
    }

}
