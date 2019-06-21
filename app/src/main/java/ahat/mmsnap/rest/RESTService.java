package ahat.mmsnap.rest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import ahat.mmsnap.ApplicationStatus;
import ahat.mmsnap.R;

public class RESTService extends IntentService
{
    public static final String REST_URL = "https://www.mm-snap.com"; //"http://192.168.1.31:8080";
    private static final String ACTION_START = "ACTION_START";
    private static Calendar lastTrasmissiondate;
    private static final int minRetransmissionSeconds = 180;

    public RESTService()
    {
        super( RESTService.class.getSimpleName() );
        lastTrasmissiondate = null;
    }

    public static Intent createIntentStart( Context context )
    {
        Intent intent = new Intent( context, RESTService.class );
        intent.setAction( ACTION_START );
        return intent;
    }


    @Override
    protected void onHandleIntent( @Nullable Intent intent )
    {
        try
        {
            String action = intent.getAction();
            if( ACTION_START.equals( action ) )
            {
                processREST();
            }
        }
        finally
        {
            WakefulBroadcastReceiver.completeWakefulIntent( intent );
        }
    }


    private boolean processByStatePolicy( ApplicationStatus.State state )
    {
        switch( state.name() )
        {
            case ApplicationStatus.InOrder.NAME:
            case ApplicationStatus.WeeklyEvaluationPending.NAME:
            case ApplicationStatus.DailyEvaluationPending.NAME:
            case ApplicationStatus.NoFinalAssessments.NAME:
            case ApplicationStatus.Finished.NAME:
                return true;
        }

        return false;
    }

    public void processREST()
    {
        // transmit at least after minRetransmissionSeconds from last transmission to allow for answer
        // and avoid retransmitting the same data multiple times
        if( null != lastTrasmissiondate )
        {
            lastTrasmissiondate.add( Calendar.SECOND, minRetransmissionSeconds );
            if( Calendar.getInstance().before( lastTrasmissiondate ) )
            {
                //no transmission yet
                //
                return;
            }
        }

        lastTrasmissiondate = Calendar.getInstance();

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );

            if( !processByStatePolicy( as.getState() ) )
            {
                return;
            }

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
            JSONObject jsonUser = new JSONObject();
            jsonUser.put( "id", settings.getInt( getString( R.string.key_remoteUserId ), -1 ) );

            transmit( as.serverData.eqvasInitial, jsonUser, "/api/e-q-vas" );
            transmit( as.serverData.eqvasFinal, jsonUser, "/api/e-q-vas" );
            for( ApplicationStatus.ServerEQVAS eqvas : as.serverData.eqvas )
            {
                transmit( eqvas, jsonUser, "/api/e-q-vas" );
            }

            transmit( as.serverData.problematicBehaviorsInitial, jsonUser, "/api/health-risks" );
            transmit( as.serverData.problematicBehaviorsFinal, jsonUser, "/api/health-risks" );
            for( ApplicationStatus.ServerProblematicBehaviors problematicBehavior : as.serverData.problematicBehaviors )
            {
                transmit( problematicBehavior, jsonUser, "/api/health-risks" );
            }

            transmit( as.serverData.selfEfficacyInitial, jsonUser, "/api/self-efficacies" );
            transmit( as.serverData.selfEfficacyFinal, jsonUser, "/api/self-efficacies" );
            transmit( as.serverData.intentionsAndPlansInitial, jsonUser, "/api/intentions-and-plans" );
            transmit( as.serverData.intentionsAndPlansFinal, jsonUser, "/api/intentions-and-plans" );
            transmit( as.serverData.selfRatedHealthInitial, jsonUser, "/api/self-rated-healths" );
            transmit( as.serverData.selfRatedHealthFinal, jsonUser, "/api/self-rated-healths" );

            for( ApplicationStatus.ServerDailyEvaluation dailyEvaluation : as.serverData.dailyEvaluations )
            {
                transmit( dailyEvaluation, jsonUser, "/api/daily-evaluations" );
            }

            for( ApplicationStatus.ServerWeeklyEvaluation weeklyEvaluation : as.serverData.weeklyEvaluations )
            {
                transmit( weeklyEvaluation, jsonUser, "/api/weekly-evaluations" );
            }

        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Failed during REST. Error: " + e.getMessage() );
        }
    }

    private void transmit( final ApplicationStatus.ToREST toREST, JSONObject jsonUser, String apiUrl )
    {
        if( null == toREST.getSubmissionDate() || null != toREST.getAcknowledgementDate() )
        {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue( this );
        String url = RESTService.REST_URL + apiUrl;

        try
        {
            JSONObject body = toREST.toREST();
            body.put( "user", jsonUser );
            JsonObjectRequest request = new AuthJsonObjectRequest(
                Request.Method.POST, url, body,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse( JSONObject response )
                    {
                        if( null != response )
                        {
                            try
                            {
                                toREST.setAcknowledgementDate( new Date() );
                                ApplicationStatus as = ApplicationStatus.getInstance( getApplicationContext() );
                                as.save();
                            }
                            catch( Exception e )
                            {
                                e.printStackTrace();
                                Log.e( "MMSNAP:", "Error transmitting to server: " + e.getMessage() );
                            }
                        }
                    }
                },
                new JWTRefreshErrorListener(
                    this,
                    new JWTRefreshErrorListener.Listener()
                    {
                        @Override
                        public void onError( VolleyError error )
                        {
                            Log.e( "MMSNAP:", "Transmission to server failed. Error: " + error.getMessage() );
                        }
                    } )
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse( VolleyError error )
//                    {
//                        Log.e( "MMSNAP:", "Transmission of EQVAS failed. Error: " + error.getMessage() );
//                    }
//                }
                );
            queue.add( request );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Error ransmitting to server: " + e.getMessage() );
        }
    }
}
