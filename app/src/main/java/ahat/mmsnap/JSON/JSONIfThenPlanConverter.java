package ahat.mmsnap.JSON;

import org.json.JSONObject;

import ahat.mmsnap.Models.IfThenPlan;

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
