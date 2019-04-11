package ahat.mmsnap.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.CopingPlan;
import ahat.mmsnap.models.IfThenPlan;

public class JSONArrayConverterCopingPlan extends JSONArrayConverterIfThenPlan
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
                copingPlans.add( (CopingPlan) jc.getPlan() );
            }
            Collections.sort( copingPlans, IfThenPlan.comparator );
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

    @Override
    public ArrayList<? extends IfThenPlan> getPlans()
    {
        return copingPlans;
    }
}
