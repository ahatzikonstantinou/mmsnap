package ahat.mmsnap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.rest.RESTService;

public class ResetPasswordActivity extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reset_password );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        findViewById( R.id.reset_password_button ).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick( final View view )
                {
                    final ProgressBar progressBar = findViewById( R.id.reset_progress );

                    EditText keyEditText = findViewById( R.id.key );
                    EditText passwordEditText = findViewById( R.id.password );
                    EditText verifyPasswordEditText = findViewById( R.id.verify_new_password );

                    String key = keyEditText.getText().toString().trim();
                    final String password = passwordEditText.getText().toString().trim();
                    String verifyPassword = verifyPasswordEditText.getText().toString().trim();

                    if( key.isEmpty() )
                    {
                        keyEditText.setError( getString( R.string.error_field_required ) );
                        keyEditText.requestFocus();
                        return;
                    }
                    if( password.isEmpty() )
                    {
                        passwordEditText.setError( getString( R.string.error_field_required ) );
                        passwordEditText.requestFocus();
                        return;
                    }
                    if( verifyPassword.isEmpty() )
                    {
                        verifyPasswordEditText.setError( getString( R.string.error_field_required ) );
                        verifyPasswordEditText.requestFocus();
                        return;
                    }
                    if( !verifyPassword.equals( password ) )
                    {
                        verifyPasswordEditText.setError( getString( R.string.error_verify_password ) );
                        verifyPasswordEditText.requestFocus();
                        return;
                    }

                    progressBar.setVisibility( View.VISIBLE );
                    RequestQueue queue = Volley.newRequestQueue( view.getContext() );
                    JSONObject body = new JSONObject();
                    try
                    {
                        body.put( "key", key );
                        body.put( "newPassword", password );
                    }
                    catch( JSONException e )
                    {
                        e.printStackTrace();
                        Log.e( "MMSNAP:", "Password reset finish failed at json body creation for key: " + key );
                        Toast.makeText( ResetPasswordActivity.this, "Password reset failed", Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility( View.GONE );
                        return;
                    }
                    JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        RESTService.REST_URL + "/api/account/reset-password/finish",
                        body,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse( JSONObject response )
                            {
                                progressBar.setVisibility( View.GONE );

                                try
                                {
                                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
                                    as.passwordHasBeenReset();

                                    String username = response.getString( "login" );
                                    String email = response.getString( "email" );
                                    int remoteUserId = response.getInt( "id" );

                                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( view.getContext() );
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putInt( getString( R.string.key_remoteUserId ), remoteUserId );
                                    editor.putString( getString( R.string.key_email ), email );
                                    editor.putString( getString( R.string.key_username ), username );
                                    editor.putString( getString( R.string.key_password ), password );
                                    editor.commit();
                                    
                                    onBeforeResetPasswordFinish( as );
                                }
                                catch( Exception e )
                                {
                                    e.printStackTrace();
                                    Toast.makeText( ResetPasswordActivity.this, "An unexpected error occurred during password reset.", Toast.LENGTH_SHORT ).show();
                                    Log.e( "MMSNAP:", "Password reset finish failed to update ApplicationStatus. Error: " + e.getMessage() );
                                    return;
                                }
                                Toast.makeText( ResetPasswordActivity.this, "Password reset was successful", Toast.LENGTH_SHORT ).show();
                                finish();
                                view.getContext().startActivity( new Intent( view.getContext(), MainActivity.class ) );
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse( VolleyError error )
                            {
                                progressBar.setVisibility( View.GONE );
                                Toast.makeText( ResetPasswordActivity.this, "Password reset failed", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    );
                    queue.add( request );
                }
            }
        );
    }

    protected void onBeforeResetPasswordFinish( ApplicationStatus applicationStatus ) throws IOException, JSONException, ConversionException
    {
    }


}
