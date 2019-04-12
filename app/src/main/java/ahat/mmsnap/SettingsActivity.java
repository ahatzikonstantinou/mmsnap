package ahat.mmsnap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.AppCompatPreferenceActivity;
import ahat.mmsnap.R;
import ahat.mmsnap.rest.App;
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
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_password)));

            Preference dialogPreference = getPreferenceScreen().findPreference( "key_username" );
            dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    final Dialog login = new Dialog( getActivity() );
                    // Set GUI of login screen
                    login.setContentView(R.layout.login_dlg );
                    final EditText usernameEditText = login.findViewById( R.id.username );
                    final EditText passwordEditText = login.findViewById( R.id.password );
                    final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );
                    usernameEditText.setText( settings.getString( getString( R.string.key_username ), ""  ) );
                    passwordEditText.setText( settings.getString( getString( R.string.key_password ), ""  ) );

                    login.findViewById( R.id.cancel_button ).setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View view )
                        {
                            login.dismiss();
                        }
                    } );
                    login.findViewById( R.id.sign_in_button ).setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View view )
                        {
                            login.findViewById( R.id.login_progress ).setVisibility( View.VISIBLE );

                            final String username = usernameEditText.getText().toString();
                            final String password = passwordEditText.getText().toString();

                            RequestQueue queue = Volley.newRequestQueue( getActivity() );
                            String url = RESTService.REST_URL + "/api/authenticate";
                            JSONObject jsonBody = new JSONObject();
                            try
                            {

                                jsonBody.put( "username", username );
                                jsonBody.put( "password", password );
                            }
                            catch( JSONException e )
                            {
                                e.printStackTrace();
                                Log.e( "MMSNAP:", "Error during preferences login: " + e.getMessage() );
                            }

                            JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST, url, jsonBody,
                                new Response.Listener<JSONObject>()
                                {
                                   @Override
                                    public void onResponse( JSONObject response )
                                    {
                                        if( null != response )
                                        {
                                            // success!

                                            // refresh jwt and get the remote user id in case the user login changed
                                            try
                                            {
                                                App.setJwtToken( response.getString( "id_token" ) );
                                                LoginActivity.getRemoteUserId( getActivity() );
                                            }
                                            catch ( JSONException e )
                                            {
                                                e.printStackTrace();
                                                Log.e( "MMSNAP:", "Error receiving login answer: " + e.getMessage() );
                                            }

                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString( getString( R.string.key_username ), username );
                                            editor.putString( getString( R.string.key_password ), password );
                                            editor.commit();

                                            findPreference( getString( R.string.key_username ) ).setSummary( username );

                                            login.dismiss();

                                            Toast.makeText( getActivity(), "Login success!", Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener()
                                {
                                   @Override
                                   public void onErrorResponse( VolleyError error)
                                   {
                                       login.findViewById( R.id.login_progress ).setVisibility( View.INVISIBLE );
                                       String message = "Login failed. Error: " + error.getMessage();
                                       Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT ).show();
                                   }
                            });
                            queue.add(request);
                        }
                    } );
                    login.show();
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