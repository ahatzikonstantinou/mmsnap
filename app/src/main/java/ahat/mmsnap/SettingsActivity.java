package ahat.mmsnap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ahat.mmsnap.rest.App;
import ahat.mmsnap.rest.AuthStringRequest;
import ahat.mmsnap.rest.JWTRefreshErrorListener;
import ahat.mmsnap.rest.RESTService;

public class SettingsActivity extends AppCompatPreferenceActivity
{
    private static final String TAG = SettingsActivity.class.getSimpleName();
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correctImageView display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                    index >= 0
                    ? listPreference.getEntries()[index]
                    : null);

            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_username")) {
                    preference.setSummary(stringValue);
                }
                else if (preference.getKey().equals("key_password")) {
                    preference.setSummary("*********");
                }
                else if (preference.getKey().equals("key_email")) {
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                                                                 PreferenceManager
                                                                     .getDefaultSharedPreferences(preference.getContext())
                                                                     .getString(preference.getKey(), ""));
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                   Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                   "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kospiro@yahoo.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString( R.string.choose_email_client)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // EditText change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_username)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_email)));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_password)));

//            Preference dialogPreference = getPreferenceScreen().findPreference( "key_username" );
//            dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                public boolean onPreferenceClick(Preference preference) {
//
//                    final Dialog login = new Dialog( getActivity() );
//                    // Set GUI of login screen
//                    login.setContentView(R.layout.login_dlg );
//                    final EditText usernameEditText = login.findViewById( R.id.username );
//                    final EditText passwordEditText = login.findViewById( R.id.password );
//                    final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );
//                    usernameEditText.setText( settings.getString( getString( R.string.key_username ), ""  ) );
//                    passwordEditText.setText( settings.getString( getString( R.string.key_password ), ""  ) );
//
//                    login.findViewById( R.id.cancel_button ).setOnClickListener( new View.OnClickListener() {
//                        @Override
//                        public void onClick( View view )
//                        {
//                            login.dismiss();
//                        }
//                    } );
//                    login.findViewById( R.id.sign_in_button ).setOnClickListener( new View.OnClickListener() {
//                        @Override
//                        public void onClick( View view )
//                        {
//                            login.findViewById( R.id.login_progress ).setVisibility( View.VISIBLE );
//
//                            final String username = usernameEditText.getText().toString();
//                            final String password = passwordEditText.getText().toString();
//
//                            RequestQueue queue = Volley.newRequestQueue( getActivity() );
//                            String url = RESTService.REST_URL + "/api/authenticate";
//                            JSONObject jsonBody = new JSONObject();
//                            try
//                            {
//
//                                jsonBody.put( "username", username );
//                                jsonBody.put( "password", password );
//                            }
//                            catch( JSONException e )
//                            {
//                                e.printStackTrace();
//                                Log.e( "MMSNAP:", "Error during preferences login: " + e.getMessage() );
//                            }
//
//                            JsonObjectRequest request = new JsonObjectRequest(
//                                Request.Method.POST, url, jsonBody,
//                                new Response.Listener<JSONObject>()
//                                {
//                                   @Override
//                                    public void onResponse( JSONObject response )
//                                    {
//                                        if( null != response )
//                                        {
//                                            // success!
//
//                                            // refresh jwt and get the remote user id in case the user login changed
//                                            try
//                                            {
//                                                App.setJwtToken( response.getString( "id_token" ) );
//                                                LoginActivity.getRemoteUserInfo( getActivity() );
//                                            }
//                                            catch ( JSONException e )
//                                            {
//                                                e.printStackTrace();
//                                                Log.e( "MMSNAP:", "Error receiving login answer: " + e.getMessage() );
//                                            }
//
//                                            SharedPreferences.Editor editor = settings.edit();
//                                            editor.putString( getString( R.string.key_username ), username );
//                                            editor.putString( getString( R.string.key_password ), password );
//                                            editor.commit();
//
//                                            findPreference( getString( R.string.key_username ) ).setSummary( username );
//
//                                            login.dismiss();
//
//                                            Toast.makeText( getActivity(), "Login success!", Toast.LENGTH_SHORT ).show();
//                                        }
//                                    }
//                                },
//                                new Response.ErrorListener()
//                                {
//                                   @Override
//                                   public void onErrorResponse( VolleyError error)
//                                   {
//                                       login.findViewById( R.id.login_progress ).setVisibility( View.INVISIBLE );
//                                       String message = "Login failed. Error: " + error.getMessage();
//                                       Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT ).show();
//                                   }
//                            });
//                            queue.add(request);
//                        }
//                    } );
//                    login.show();
//                    return true;
//                }
//            });


            Preference dialogResetPasswordPreference = getPreferenceScreen().findPreference( "key_reset_password" );
            dialogResetPasswordPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener()
                {
                    @Override
                    public boolean onPreferenceClick( Preference preference )
                    {
                        final AlertDialog resetPasswordDialog = new AlertDialog.Builder( getActivity() )
                            .setTitle( getActivity().getString( R.string.dlg_reset_password_title) )
                            .setCancelable( true )
                            .setMessage( "You will receive a key in the email account that you have registered with MMSNAP. Use this key to reset your password." )
                            .setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick( DialogInterface dialogInterface, int i )
                                {
                                    dialogInterface.cancel();
                                }
                            } )
                            .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick( final DialogInterface dialogInterface, int i )
                                {
                                    final Context context = getActivity();
                                    // get email
                                    final String email = PreferenceManager.getDefaultSharedPreferences( context ).getString( getActivity().getString( R.string.key_email ), "" );
                                    if( email.isEmpty() )
                                    {
                                        dialogInterface.dismiss();
                                        Toast.makeText( context, "No email found", Toast.LENGTH_SHORT ).show();
                                        return;
                                    }

                                    // start password reset
                                    RequestQueue queue =  Volley.newRequestQueue( context );
                                    StringRequest request = new StringRequest(
                                        Request.Method.POST,
                                        RESTService.REST_URL + "/api/account/reset-password/init",
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse( String response )
                                            {
                                                try
                                                {
                                                    ApplicationStatus as = ApplicationStatus.getInstance( context );
                                                    as.resetPassword();
                                                    dialogInterface.dismiss();
                                                    context.startActivity( new Intent( context, ResetPasswordActivity.class ) );
                                                }
                                                catch( Exception e )
                                                {
                                                    e.printStackTrace();
                                                    String message = "Password reset failed for email " + email;
                                                    Log.e( "MMSNAP:", message + ". Error: " + e.getMessage() );
                                                    Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse( VolleyError error )
                                            {
                                                String message = "Password reset failed for email " + email;
                                                Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
                                            }
                                        }
                                    )
                                    {
                                        // override this because String request sends header "Content-Type","application/x-www-form-urlencoded" which distorts the body content
                                        // while the api requires "Content-Type", "application/json"
                                        @Override    public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> headers = new HashMap<String, String>();
                                            headers.put("Content-Type", "application/json");
                                            return headers;
                                        }

                                        //override this because api requires that the body is only the email
                                        @Override
                                        public byte[] getBody() throws AuthFailureError
                                        {
                                            String httpPostBody = email;
                                            return httpPostBody.getBytes();
                                        }
                                    };
                                    queue.add( request );
                                }
                            } )
                            .create();
                        resetPasswordDialog.show();
                        return false;
                    }
}
            );



            Preference dialogPasswordPreference = getPreferenceScreen().findPreference( "key_password" );
            dialogPasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    final Dialog chngPswd = new Dialog( getActivity() );
                    // Set GUI of login screen
                    chngPswd.setContentView(R.layout.change_password_dlg );
                    final EditText oldPasswordEditText = chngPswd.findViewById( R.id.old_password );
                    final EditText newPasswordEditText = chngPswd.findViewById( R.id.new_password );
                    final EditText vewrifyNewPasswordEditText = chngPswd.findViewById( R.id.verify_new_password );

                    //debugging
//                    oldPasswordEditText.setText( "user" );
//                    newPasswordEditText.setText( "user" );
//                    vewrifyNewPasswordEditText.setText( "user" );

                    final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );

                    chngPswd.findViewById( R.id.cancel_button ).setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View view )
                        {
                            chngPswd.dismiss();
                        }
                    } );
                    chngPswd.findViewById( R.id.change_button ).setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View view )
                        {
                            final String oldPassword = oldPasswordEditText.getText().toString();
                            final String newPassword = newPasswordEditText.getText().toString();
                            final String verifyNewPassword = vewrifyNewPasswordEditText.getText().toString();

                            if( oldPassword.isEmpty() )
                            {
                                oldPasswordEditText.setError( getString( R.string.error_field_required ) );
                                oldPasswordEditText.requestFocus();
                                return;
                            }

                            if( newPassword.isEmpty() )
                            {
                                newPasswordEditText.setError( getString( R.string.error_field_required ) );
                                newPasswordEditText.requestFocus();
                                return;
                            }

                            if( verifyNewPassword.isEmpty() )
                            {
                                vewrifyNewPasswordEditText.setError( getString( R.string.error_field_required ) );
                                vewrifyNewPasswordEditText.requestFocus();
                                return;
                            }

                            if( !verifyNewPassword.equals( newPassword ) )
                            {
                                vewrifyNewPasswordEditText.setError( getString( R.string.error_verify_password ) );
                                vewrifyNewPasswordEditText.requestFocus();
                                return;
                            }

                            chngPswd.findViewById( R.id.change_password_progress ).setVisibility( View.VISIBLE );
                            RequestQueue queue = Volley.newRequestQueue( getActivity() );
                            String url = RESTService.REST_URL + "/api/account/change-password ";

                            final AuthStringRequest request = new AuthStringRequest(
                                Request.Method.POST, url,
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse( String response )
                                    {
                                        if( null != response )
                                        {
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString( getString( R.string.key_password ), newPassword );
                                            editor.commit();

                                            chngPswd.dismiss();

                                            Toast.makeText( getActivity(), "Password changed successfully!", Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                },
                                new JWTRefreshErrorListener(
                                        getActivity(),
                                        oldPassword,
                                        new JWTRefreshErrorListener.Listener()
                                        {
                                            @Override
                                            public void onError( VolleyError error )
                                            {
                                                chngPswd.findViewById( R.id.change_password_progress ).setVisibility( View.INVISIBLE );
                                                String message = "Password change failed. Error: " + error.getMessage();
                                                Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT ).show();
                                            }
                                        }
                                    )
                                )
                                {
                                    // override this because String request sends header "Content-Type","application/x-www-form-urlencoded" which distorts the body content
                                    // while the api requires "Content-Type", "application/json"
                                    @Override    public Map<String, String> getHeaders() throws AuthFailureError
                                    {
                                        // be carefull to get headers from super because it contains the jwt bearer header
                                        Map<String, String> headers = super.getHeaders();
                                        headers.put("Content-Type", "application/json");
                                        return headers;
                                    }

                                    // override this because api requires that the body is only the new password
                                    @Override
                                    public byte[] getBody() throws AuthFailureError
                                    {
                                        String httpPostBody = newPassword;
                                        return httpPostBody.getBytes();
                                    }
                                };
                            queue.add(request);
                        }
                    } );
                    chngPswd.show();
                    return true;
                }
            });


            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
        }
    }
}