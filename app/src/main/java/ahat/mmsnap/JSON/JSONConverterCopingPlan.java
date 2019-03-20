package ahat.mmsnap.JSON;

import android.provider.CalendarContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.ApplicationStatus;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;
import ahat.mmsnap.Models.Reminder;

public class JSONConverterCopingPlan extends JSONIfThenPlanConverter
{

    private CopingPlan plan;

    @Override
    public IfThenPlan getPlan()
    {
        return plan;
    }

    @Override
    public void from() throws ConversionException
    {
        try
        {
            ArrayList<ApplicationStatus.Behavior> targetBehaviors = new ArrayList<>();
            JSONArray jsonBehaviors = jsonObject.getJSONArray( "behaviors" );
            for( int i = 0 ; i < jsonBehaviors.length() ; i++ )
            {
                targetBehaviors.add( ApplicationStatus.Behavior.valueOf( ( (JSONObject) jsonBehaviors.get( i ) ).getString( "name" ) ) );
            }

            ArrayList<IfThenPlan.WeekDay> days = new ArrayList<>();
            JSONArray jsonDays = jsonObject.getJSONArray( "days" );
            for( int i = 0 ; i < jsonDays.length() ; i++ )
            {
                JSONObject jsonDay = (JSONObject) jsonDays.get( i );
                IfThenPlan.WeekDay d = IfThenPlan.WeekDay.valueOf( jsonDay.getString( "name" ) );
                days.add( d );
            }

            ArrayList<Reminder> reminders = new ArrayList<>();
            JSONArray jsonReminders = jsonObject.getJSONArray( "reminders" );
            for( int i = 0 ; i < jsonReminders.length() ; i++ )
            {
                JSONObject jsonReminder = jsonReminders.getJSONObject( i );
                reminders.add( new Reminder( jsonReminder.getInt( "hour" ), jsonReminder.getInt( "minute" ) ) );
            }
            plan = new CopingPlan(
                jsonObject.getInt( "id" ),
                jsonObject.getString( "if" ),
                jsonObject.getString( "then" ),
                jsonObject.getBoolean( "active" ),
                jsonObject.getInt( "year" ),
                jsonObject.getInt( "weekOfYear" ),
                targetBehaviors,
                days,
                reminders
            );

        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    @Override
    public void to() throws ConversionException
    {
        try
        {
            jsonObject.put( "id", String.valueOf( plan.id ) );
            jsonObject.put( "if", plan.ifStatement );
            jsonObject.put( "then", plan.thenStatement );
            jsonObject.put( "active", plan.active );
            jsonObject.put( "year", plan.year);
            jsonObject.put( "weekOfYear", plan.weekOfYear);

            JSONArray jsonBehaviors = new JSONArray();
            for( int i = 0 ; i < plan.targetBehaviors.size() ; i++ )
            {
                JSONObject jsonBehavior = new JSONObject();
                jsonBehavior.put( "name", plan.targetBehaviors.get( i ).name() );
                jsonBehaviors.put( jsonBehavior );
            }
            jsonObject.put( "behaviors", jsonBehaviors );

            JSONArray jsonDays = new JSONArray();
            for( int i = 0 ; i < plan.days.size() ; i++ )
            {
                JSONObject jsonDay = new JSONObject();
                jsonDay.put( "name", plan.days.get( i ).name() );
                jsonDays.put( jsonDay );
            }
            jsonObject.put( "days", jsonDays );

            JSONArray jsonReminders = new JSONArray();
            for( Reminder reminder : plan.reminders )
            {
                JSONObject jsonReminder = new JSONObject();
                jsonReminder.put( "hour", reminder.hour );
                jsonReminder.put( "minute", reminder.minute );

                jsonReminders.put( jsonReminder );
            }
            jsonObject.put( "reminders", jsonReminders );
        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    public JSONConverterCopingPlan( JSONObject jsonObject )
    {
        super( jsonObject );
    }

    public JSONConverterCopingPlan( CopingPlan plan )
    {
        super();
        this.plan = plan;
    }

    public JSONConverterCopingPlan( IfThenPlan plan )
    {
        super();
        this.plan = new CopingPlan( plan );
    }

    public JSONConverterCopingPlan()
    {
        super();
    }

}
