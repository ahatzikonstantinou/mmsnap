package ahat.mmsnap.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.DailyEvaluation;
import ahat.mmsnap.Models.WeeklyEvaluation;

public class JSONArrayConverterDailyEvaluation extends JSONArrayConverter
{

    private ArrayList<DailyEvaluation> dailyEvaluations;
    public ArrayList<DailyEvaluation> getDailyEvaluations()
    {
        return dailyEvaluations;
    }

    @Override
    public void from() throws ConversionException
    {
        dailyEvaluations.clear();
        try
        {
            for( int i = 0 ; i < jsonArray.length() ; i++ )
            {
                JSONConverterDailyEvaluation jc = new JSONConverterDailyEvaluation( (JSONObject) jsonArray.get( i ) );
                jc.from();
                dailyEvaluations.add( jc.getDailyEvaluation() );
            }
        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    @Override
    public void to() throws ConversionException
    {
        jsonArray = new JSONArray();
        for( int i = 0; i < dailyEvaluations.size(); i++ )
        {
            JSONConverterDailyEvaluation jc = new JSONConverterDailyEvaluation( dailyEvaluations.get( i ) );
            jc.to();
            jsonArray.put( jc.getJsonObject() );
        }
    }

    public JSONArrayConverterDailyEvaluation()
    {
        super();
        dailyEvaluations = new ArrayList<>();
    }

    public JSONArrayConverterDailyEvaluation( JSONArray jsonArray )
    {
        super( jsonArray );
        dailyEvaluations = new ArrayList<>();
    }

    public JSONArrayConverterDailyEvaluation( ArrayList<DailyEvaluation> dailyEvaluations )
    {
        super();
        this.dailyEvaluations = dailyEvaluations;
    }

}
