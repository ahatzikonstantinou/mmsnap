package ahat.mmsnap.json;

import org.json.JSONArray;

import java.util.ArrayList;

import ahat.mmsnap.models.IfThenPlan;

public abstract class JSONArrayConverterIfThenPlan extends JSONArrayConverter
{
    public JSONArrayConverterIfThenPlan()
    {
        super();
    }

    public JSONArrayConverterIfThenPlan( JSONArray jsonArray )
    {
        super( jsonArray );
    }

    public abstract ArrayList<? extends IfThenPlan> getPlans();
}
