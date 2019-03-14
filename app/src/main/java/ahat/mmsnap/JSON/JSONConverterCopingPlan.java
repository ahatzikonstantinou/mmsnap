package ahat.mmsnap.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.ApplicationStatus;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;

public class JSONConverterCopingPlan extends JSONObjectConverter
{

    private CopingPlan plan;
    public CopingPlan getPlan()
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
            ArrayList<IfThenPlan.Day> days = new ArrayList<>();
            JSONArray jsonDays = jsonObject.getJSONArray( "days" );
            for( int i = 0 ; i < jsonDays.length() ; i++ )
            {
                JSONObject jsonDay = (JSONObject) jsonDays.get( i );
                IfThenPlan.Day d = new CopingPlan().new Day( IfThenPlan.WeekDay.valueOf( jsonDay.getString( "name" ) ) );
                d.setEvaluated( jsonDay.getBoolean( "evaluated" ) );
                d.setSuccessful( jsonDay.getBoolean( "successful" ) );
                days.add( d );
            }
            plan = new CopingPlan(
                jsonObject.getInt( "id" ),
                jsonObject.getString( "if" ),
                jsonObject.getString( "then" ),
                jsonObject.getBoolean( "active" ),
                jsonObject.getInt( "year" ),
                jsonObject.getInt( "weekOfYear" ),
                targetBehaviors,
                days
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
                jsonDay.put( "name", plan.days.get( i ).getWeekDay().name() );
                jsonDay.put( "evaluated", plan.days.get( i ).isEvaluated() );
                jsonDay.put( "successful", plan.days.get( i ).isSuccessful() );
                jsonDays.put( jsonDay );
            }
            jsonObject.put( "days", jsonDays );
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
