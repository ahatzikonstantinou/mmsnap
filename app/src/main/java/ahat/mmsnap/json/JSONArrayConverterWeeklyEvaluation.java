package ahat.mmsnap.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.WeeklyEvaluation;

public class JSONArrayConverterWeeklyEvaluation extends JSONArrayConverter
{

    private ArrayList<WeeklyEvaluation> weeklyEvaluations;
    public ArrayList<WeeklyEvaluation> getWeeklyEvaluations()
    {
        return weeklyEvaluations;
    }

    @Override
    public void from() throws ConversionException
    {
        weeklyEvaluations.clear();
        try
        {
            for( int i = 0 ; i < jsonArray.length() ; i++ )
            {
                JSONConverterWeeklyEvaluation jc = new JSONConverterWeeklyEvaluation( (JSONObject) jsonArray.get( i ) );
                jc.from();
                weeklyEvaluations.add( jc.getWeeklyEvaluation() );
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
        for( int i = 0; i < weeklyEvaluations.size(); i++ )
        {
            JSONConverterWeeklyEvaluation jc = new JSONConverterWeeklyEvaluation( weeklyEvaluations.get( i ) );
            jc.to();
            jsonArray.put( jc.getJsonObject() );
        }
    }

    public JSONArrayConverterWeeklyEvaluation()
    {
        super();
        weeklyEvaluations = new ArrayList<>();
    }

    public JSONArrayConverterWeeklyEvaluation( JSONArray jsonArray )
    {
        super( jsonArray );
        weeklyEvaluations = new ArrayList<>();
    }

    public JSONArrayConverterWeeklyEvaluation( ArrayList<WeeklyEvaluation> weeklyEvaluations )
    {
        super();
        this.weeklyEvaluations = weeklyEvaluations;
    }

}
