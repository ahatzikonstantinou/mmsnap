package ahat.mmsnap.Models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
    private String filePath;

    public enum KeyPart { YEAR, WEEK_OF_YEAR, WEEK_DAY, HOUR, MINUTE }

    private static final String FILENAME = "alarms.json";
    private static Context context;
    HashMap<String, Integer> alarms;
    private static ReminderAlarms instance = null;

    private ReminderAlarms()
    {
        filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        alarms = new HashMap<>();
    }

    public static ReminderAlarms getInstance( Context context ) throws IOException, JSONException
    {
        ReminderAlarms.context = context;
        if( null == instance )
        {
            instance = new ReminderAlarms();
            instance.read();
        }
        return instance;
    }

    public HashMap<String, Integer> getAlarms()
    {
        return alarms;
    }

    public static String generateKey( int year, int weekOfYear, WeekDay weekDay, int hour, int minute )
    {
        return String.valueOf( year ) +
               "_" + String.valueOf( weekOfYear ) +
               "_" + weekDay.name() +
               "_" + String.valueOf( hour ) +
               "_" + String.valueOf( minute );
    }

    public static int parseKey( KeyPart keyPart, String key )
    {
        String[] parts = key.split( "_" );
        switch( keyPart )
        {
            case YEAR:
                return Integer.parseInt( parts[0] );
            case WEEK_OF_YEAR:
                return Integer.parseInt( parts[1] );
            case WEEK_DAY:
                WeekDay weekDay = WeekDay.valueOf( parts[2] );
                return weekDay.ordinal();
            case HOUR:
                return Integer.parseInt( parts[3] );
            case MINUTE:
                return Integer.parseInt( parts[4] );
        }
        return -1;
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
            alarms.remove( key ); //this will cause a ConcurrentModificationException when removing multiple reminder alarms over of for loop for example
            alarmCanBeCancelled = true;
        }
        else
        {
            alarms.put( key, count - 1 );
        }

        write();
        return alarmCanBeCancelled;
    }

    public String add( int year, int weekOfYear, WeekDay weekDay, int hour, int minute, boolean recordAlarmOnlyIfNotExists ) throws IOException, JSONException
    {
        String key = generateKey( year, weekOfYear, weekDay, hour, minute );
        int count = 0;
        boolean save = false;
        if( alarms.containsKey( key ) )
        {
            if( !recordAlarmOnlyIfNotExists )
            {
                count = alarms.get( key );
                alarms.put( key, count + 1 );
                save = true;
            }
        }
        else
        {
            alarms.put( key, count + 1 );
            save = true;
        }


        if( save )
        {
            write();
        }

        return key;
    }

    public void write() throws IOException, JSONException
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

    public void read() throws IOException, JSONException
    {
        alarms.clear();
        JSONArray jsonArray = JSONArrayIOHandler.loadItems( filePath );
        for( int i = 0 ; i < jsonArray.length() ; i++ )
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get( i );
            alarms.put( jsonObject.getString( "key" ), jsonObject.getInt( "count" ) );
        }
    }

}
