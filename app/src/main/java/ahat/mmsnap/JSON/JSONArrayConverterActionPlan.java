package ahat.mmsnap.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

public class JSONArrayConverterActionPlan extends JSONArrayConverterIfThenPlan
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
                actionPlans.add( (ActionPlan) jc.getPlan() );
            }
            Collections.sort( actionPlans, IfThenPlan.comparator );
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

    @Override
    public ArrayList<? extends IfThenPlan> getPlans()
    {
        return actionPlans;
    }
}
