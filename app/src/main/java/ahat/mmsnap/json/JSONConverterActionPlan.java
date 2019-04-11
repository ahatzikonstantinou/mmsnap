package ahat.mmsnap.json;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.IfThenPlan;

public class JSONConverterActionPlan extends JSONIfThenPlanConverter
{

    private ActionPlan plan;
    public IfThenPlan getPlan()
    {
        return plan;
    }

    @Override
    public void from() throws ConversionException
    {
        try
        {
            JSONConverterCopingPlan jc = new JSONConverterCopingPlan( jsonObject );
            jc.from();
            plan = new ActionPlan(
                jc.getPlan(),
                jsonObject.getString( "copingIf" ),
                jsonObject.getString( "copingThen" )
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
            JSONConverterCopingPlan jc = new JSONConverterCopingPlan( plan );
            jc.to();

            jsonObject = jc.getJsonObject();
            jsonObject.put( "copingIf", plan.copingIfStatement );
            jsonObject.put( "copingThen", plan.copingThenStatement );
        }
        catch( JSONException e )
        {
            throw new ConversionException( e );
        }
    }

    public JSONConverterActionPlan( JSONObject jsonObject )
    {
        super( jsonObject );
    }

    public JSONConverterActionPlan( ActionPlan plan )
    {
        super();
        this.plan = plan;
    }

    public JSONConverterActionPlan()
    {
        super();
    }

}
