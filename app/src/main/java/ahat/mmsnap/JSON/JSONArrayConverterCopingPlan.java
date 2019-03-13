package ahat.mmsnap.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;

public class JSONArrayConverterCopingPlan extends JSONArrayConverter
{

    private ArrayList<CopingPlan> copingPlans;
    public ArrayList<CopingPlan> getCopingPlans()
    {
        return copingPlans;
    }

    @Override
    public void from() throws ConversionException
    {
        copingPlans.clear();
        try
        {
            for( int i = 0 ; i < jsonArray.length() ; i++ )
            {
                JSONConverterCopingPlan jc = new JSONConverterCopingPlan( (JSONObject) jsonArray.get( i ) );
                jc.from();
                copingPlans.add( jc.getPlan() );
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
        for( int i = 0; i < copingPlans.size(); i++ )
        {
            JSONConverterCopingPlan jc = new JSONConverterCopingPlan( copingPlans.get( i ) );
            jc.to();
            jsonArray.put( jc.getJsonObject() );
        }
    }

    public JSONArrayConverterCopingPlan()
    {
        super();
        copingPlans = new ArrayList<>();
    }

    public JSONArrayConverterCopingPlan( JSONArray jsonArray )
    {
        super( jsonArray );
        copingPlans = new ArrayList<>();
    }

    public JSONArrayConverterCopingPlan( ArrayList<CopingPlan> copingPlans )
    {
        super();
        this.copingPlans = copingPlans;
    }

}
