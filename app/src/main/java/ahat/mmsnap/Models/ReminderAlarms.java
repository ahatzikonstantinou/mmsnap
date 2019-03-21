package ahat.mmsnap.Models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ahat.mmsnap.JSON.JSONArrayIOHandler;
import ahat.mmsnap.Models.IfThenPlan.WeekDay;

/*
 * Alarms are generated based on time only. Multiple reminders (i.e. plans) may use the same alarm to generate reminder notifications
 * for their if-then statements.
 * ReminderAlarms keeps count of how many reminders use a particular alarm. When no reminder is using an alarma, the remove function
 * returns true meaning that this alarm can be cancelled
 */
public class ReminderAlarms
{
    private static final String FILENAME = "alarms.json";
    private static Context context;
    HashMap<String, Integer> alarms;
    private static ReminderAlarms instance = null;

    private ReminderAlarms()
    {
        alarms = new HashMap<>();
    }

    public static ReminderAlarms getInstance( Context context ) throws IOException, JSONException
    {
        ReminderAlarms.context = context;
        if( null == instance )
        {
            instance = new ReminderAlarms();
            instance.read( context.getFilesDir().getPath() + "/" + FILENAME );
        }
        return instance;
    }

    public static String generateKey( int year, int weekOfYear, WeekDay weekDay, int hour, int minute )
    {
        return String.valueOf( year ) +
               "_" + String.valueOf( weekOfYear ) +
               "_" + weekDay.name() +
               "_" + String.valueOf( hour ) +
               "_" + String.valueOf( minute );
    }

    public boolean remove( int year, int weekOfYear, WeekDay weekDay, int hour, int minute ) throws IOException, JSONException
    {
        String key = generateKey( year, weekOfYear, weekDay, hour, minute );

        if( !alarms.containsKey( key ) )
        {
            return true;
        }

        int count = alarms.get( key );

        // if there is only one reminder using this alarm, remove the key and return true so that the alarm may be cancelled
        boolean alarmCanBeCancelled = false;
        if( 1 == count )
        {
            alarms.remove( key );
            alarmCanBeCancelled = true;
        }
        else
        {
            alarms.put( key, count - 1 );
        }

        write( context.getFilesDir().getPath() + "/" + FILENAME );
        return alarmCanBeCancelled;
    }

    public String add( int year, int weekOfYear, WeekDay weekDay, int hour, int minute ) throws IOException, JSONException
    {
        String key = generateKey( year, weekOfYear, weekDay, hour, minute );
        int count = 0;
        if( alarms.containsKey( key ) )
        {
            count = alarms.get( key );
        }
        alarms.put( key, count + 1 );

        write( context.getFilesDir().getPath() + "/" + FILENAME );

        return key;
    }

    private void write( String filePath ) throws IOException, JSONException
    {
        JSONArray jsonArray = new JSONArray();
        for( Map.Entry<String, Integer> entry : alarms.entrySet() )
        {
            String key = entry.getKey();
            int count = entry.getValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "key", key );
            jsonObject.put( "count", count );
            jsonArray.put( jsonObject );
        }

        JSONArrayIOHandler.saveItems( context, jsonArray, filePath );
    }

    private HashMap<String, Integer> read( String filePath ) throws IOException, JSONException
    {
        alarms.clear();
        JSONArray jsonArray = JSONArrayIOHandler.loadItems( filePath );
        for( int i = 0 ; i < jsonArray.length() ; i++ )
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get( i );
            alarms.put( jsonObject.getString( "key" ), jsonObject.getInt( "count" ) );
        }
        return alarms;
    }

}
