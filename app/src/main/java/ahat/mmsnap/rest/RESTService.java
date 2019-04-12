package ahat.mmsnap.rest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class RESTService extends IntentService
{
    public static final String REST_URL = "http://192.168.1.31:8081";
    private static final String ACTION_START = "ACTION_START";

    public RESTService()
    {
        super( RESTService.class.getSimpleName() );
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


    private void processREST()
    {
        try
        {

        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Failed during REST. Error: " + e.getMessage() );
        }
    }
}
