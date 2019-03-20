package ahat.mmsnap.JSON;

import org.json.JSONArray;

import java.util.ArrayList;

import ahat.mmsnap.Models.IfThenPlan;

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
