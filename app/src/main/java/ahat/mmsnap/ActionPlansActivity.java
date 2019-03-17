package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterActionPlan;
import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

public class ActionPlansActivity extends IfThenListActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    protected IfThenListAdapter createListAdapter()
    {
        return new IfThenListAdapter( this, items, selectItemsMode );
    }

    protected String getLoadItemsErrorMessage()
    {
        return "Action plans could not be loaded.";
    }

    protected String getDeleteItemsErrorMessage()
    {
        return "Could not delete selected action plans";
    }

    protected int getSubtitleStringResId()
    {
        return R.string.title_activity_action_plans;
    }

    protected int getLogoDrawableResId()
    {
        return R.drawable.if_then_section_logo;
    }

    protected Class<?> getDetailActivityClass()
    {
        return ActionPlansDetailActivity.class;
    }

    @Override
    protected ArrayList<IfThenPlan> loadItems() throws IOException, JSONException, ConversionException
    {
        ActionPlansStorage aps = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jc = new JSONArrayConverterActionPlan();
        aps.read( jc );
        ArrayList<ActionPlan> items = jc.getActionPlans();

        ArrayList<IfThenPlan> returnArray = new ArrayList<>( items.size() );
        for( ActionPlan p : items )
        {
            returnArray.add( p );
        }
        return returnArray;
    }

    @Override
    protected void saveItems() throws IOException, JSONException, ConversionException
    {
        ArrayList<ActionPlan> plans = new ArrayList<>( items.size() );
        for( IfThenPlan p : items )
        {
            plans.add( (ActionPlan) p );
        }
        ActionPlansStorage aps = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jc = new JSONArrayConverterActionPlan( plans );
        aps.write( jc );
    }

    @Override
    protected void putItemInIntent( Intent intent, int itemIndex )
    {
        intent.putExtra( "action_plan", items.get( itemIndex ) );
    }

}
