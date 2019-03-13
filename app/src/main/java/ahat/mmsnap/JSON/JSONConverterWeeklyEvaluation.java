package ahat.mmsnap.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.WeeklyEvaluation;

public class JSONConverterWeeklyEvaluation extends JSONObjectConverter
{

    private WeeklyEvaluation weeklyEvaluation;
    public WeeklyEvaluation getWeeklyEvaluation()
    {
        return weeklyEvaluation;
    }

    @Override
    public void from() throws ConversionException
    {
        try
        {
            weeklyEvaluation = new WeeklyEvaluation(
                jsonObject.getInt( "year" ),
                jsonObject.getInt( "week" ),
                jsonObject.getBoolean( "target_diet" ),
                jsonObject.getBoolean( "target_smoking" ),
                jsonObject.getBoolean( "target_physical_activity" ),
                jsonObject.getBoolean( "target_alcohol" ),
                jsonObject.getInt( "diet" ),
                jsonObject.getInt( "smoking" ),
                jsonObject.getInt( "physical_activity" ),
                jsonObject.getInt( "alcohol" )
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
            jsonObject.put( "year", weeklyEvaluation.getYear() );
            jsonObject.put( "week", weeklyEvaluation.getWeekOfYear() );
            jsonObject.put( "target_diet", weeklyEvaluation.targetDiet );
            jsonObject.put( "target_smoking", weeklyEvaluation.targetSmoking );
            jsonObject.put( "target_physical_activity", weeklyEvaluation.targetPhysicalActivity );
            jsonObject.put( "target_alcohol", weeklyEvaluation.targetAlcohol );
            jsonObject.put( "diet", weeklyEvaluation.getDiet() );
            jsonObject.put( "smoking", weeklyEvaluation.getSmoking() );
            jsonObject.put( "physical_activity", weeklyEvaluation.getPhysicalActivity() );
            jsonObject.put( "alcohol", weeklyEvaluation.getAlcohol() );
        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    public JSONConverterWeeklyEvaluation( JSONObject jsonObject )
    {
        super( jsonObject );
    }

    public JSONConverterWeeklyEvaluation( WeeklyEvaluation weeklyEvaluation )
    {
        super();
        this.weeklyEvaluation = weeklyEvaluation;
    }

    public JSONConverterWeeklyEvaluation()
    {
        super();
    }

}
