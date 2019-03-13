package ahat.mmsnap.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;

public class JSONArrayConverterActionPlan extends JSONArrayConverter
{

    private ArrayList<ActionPlan> actionPlans;
    public ArrayList<ActionPlan> getActionPlans()
    {
        return actionPlans;
    }

    @Override
    public void from() throws ConversionException
    {
        actionPlans.clear();
        try
        {
            for( int i = 0 ; i < jsonArray.length() ; i++ )
            {
                JSONConverterActionPlan jc = new JSONConverterActionPlan( (JSONObject) jsonArray.get( i ) );
                jc.from();
                actionPlans.add( jc.getPlan() );
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
        for( int i = 0; i < actionPlans.size(); i++ )
        {
            JSONConverterActionPlan jc = new JSONConverterActionPlan( actionPlans.get( i ) );
            jc.to();
            jsonArray.put( jc.getJsonObject() );
        }
    }

    public JSONArrayConverterActionPlan()
    {
        super();
        actionPlans = new ArrayList<>();
    }

    public JSONArrayConverterActionPlan( JSONArray jsonArray )
    {
        super( jsonArray );
        actionPlans = new ArrayList<>();
    }

    public JSONArrayConverterActionPlan( ArrayList<ActionPlan> actionPlans )
    {
        super();
        this.actionPlans = actionPlans;
    }

}
