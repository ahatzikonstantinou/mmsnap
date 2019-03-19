package ahat.mmsnap.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.DailyEvaluation;
import ahat.mmsnap.Models.IfThenPlan;

public class JSONConverterDailyEvaluation extends JSONObjectConverter
{

    private DailyEvaluation dailyEvaluation;
    public DailyEvaluation getDailyEvaluation()
    {
        return dailyEvaluation;
    }

    @Override
    public void from() throws ConversionException
    {
        try
        {
            JSONObject jsonPlan = jsonObject.getJSONObject( "plan" );
            JSONIfThenPlanConverter jc;
            switch( jsonPlan.getString( "type" ) )
            {
                case "action":
                    jc = new JSONConverterActionPlan( jsonPlan );
                    break;
                case "coping":
                    jc = new JSONConverterCopingPlan( jsonPlan );
                    break;
                default:
                    throw new ConversionException( new Exception( "Unknown plan type " + jsonPlan.getString( "type" ) ) );
            }
            jc.from();
            dailyEvaluation = new DailyEvaluation(
                jsonObject.getInt( "id" ),
                jc.getPlan(),
                IfThenPlan.WeekDay.valueOf( jsonObject.getString( "weekday" ) ),
                jsonObject.getBoolean( "evaluated" ),
                jsonObject.getBoolean( "successful" )
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
            jsonObject.put( "id", dailyEvaluation.id );
            jsonObject.put( "weekday", dailyEvaluation.getWeekDay().name() );
            jsonObject.put( "evaluated", dailyEvaluation.isEvaluated() );
            jsonObject.put( "successful", dailyEvaluation.isSuccessful() );
            JSONIfThenPlanConverter jc;
            String planType;
            if( dailyEvaluation.plan instanceof ActionPlan )
            {
                jc = new JSONConverterActionPlan( (ActionPlan) dailyEvaluation.plan );
                planType = "action";
            }
            else if( dailyEvaluation.plan instanceof CopingPlan )
            {
                jc = new JSONConverterCopingPlan( (CopingPlan) dailyEvaluation.plan );
                planType = "coping";
            }
            else
            {
                throw new ConversionException( new Exception( "Add new type of if-then plan to JSONConverterDailyEvaluation.to()" )  );
            }
            jc.to();
            JSONObject jsonPlan = jc.getJsonObject();
            jsonPlan.put( "type", planType );
            jsonObject.put( "plan", jsonPlan );
        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    public JSONConverterDailyEvaluation( JSONObject jsonObject )
    {
        super( jsonObject );
    }

    public JSONConverterDailyEvaluation( DailyEvaluation dailyEvaluation )
    {
        super();
        this.dailyEvaluation = dailyEvaluation;
    }

    public JSONConverterDailyEvaluation()
    {
        super();
    }

}
