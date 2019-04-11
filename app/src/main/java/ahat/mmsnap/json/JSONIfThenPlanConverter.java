package ahat.mmsnap.json;

import org.json.JSONObject;

import ahat.mmsnap.models.IfThenPlan;

public abstract class JSONIfThenPlanConverter extends JSONObjectConverter
{

    public JSONIfThenPlanConverter()
    {
        super();
    }

    public JSONIfThenPlanConverter( JSONObject jsonObject )
    {
        super( jsonObject );
    }

    public abstract IfThenPlan getPlan();
}
