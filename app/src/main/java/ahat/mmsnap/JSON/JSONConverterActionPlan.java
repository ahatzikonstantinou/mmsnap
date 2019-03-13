package ahat.mmsnap.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;

public class JSONConverterActionPlan extends JSONObjectConverter
{

    private ActionPlan plan;
    public ActionPlan getPlan()
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
//            jsonObject.put( "id", String.valueOf( plan.id ) );
//            jsonObject.put( "if", plan.ifStatement );
//            jsonObject.put( "then", plan.thenStatement );
//            jsonObject.put( "copingIf", plan.copingIfStatement );
//            jsonObject.put( "copingThen", plan.copingThenStatement );
//            jsonObject.put( "active", plan.active );
//            jsonObject.put( "year", plan.year);
//            jsonObject.put( "weekOfYear", plan.weekOfYear);
//
//            JSONArray jsonBehaviors = new JSONArray();
//            for( int i = 0 ; i < plan.targetBehaviors.size() ; i++ )
//            {
//                JSONObject jsonBehavior = new JSONObject();
//                jsonBehavior.put( "name", plan.targetBehaviors.get( i ).name() );
//                jsonBehaviors.put( jsonBehavior );
//            }
//            jsonObject.put( "behaviors", jsonBehaviors );
//
//            JSONArray jsonDays = new JSONArray();
//            for( int i = 0 ; i < plan.days.size() ; i++ )
//            {
//                JSONObject jsonDay = new JSONObject();
//                jsonDay.put( "name", plan.days.get( i ).name() );
//                jsonDay.put( "evaluated", plan.days.get( i ).isEvaluated() );
//                jsonDay.put( "successful", plan.days.get( i ).isSuccessful() );
//                jsonDays.put( jsonDay );
//            }
//            jsonObject.put( "days", jsonDays );
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
