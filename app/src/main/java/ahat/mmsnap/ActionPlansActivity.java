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

public class ActionPlansActivity extends IfThenListActivity
{

//    public static final String FILENAME = "action_plans.json";

    protected ArrayList<ActionPlan> items = new ArrayList<ActionPlan>();

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    protected IfThenListAdapter createListAdapter()
    {
        return new ActionPlansListAdapter( this, items, delete );
    }

//    protected String getFilename()
//    {
//        return FILENAME;
//    }

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
    protected void loadItems() throws IOException, JSONException, ConversionException
    {
        ActionPlansStorage aps = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jc = new JSONArrayConverterActionPlan();
        aps.read( jc );
        items = jc.getActionPlans();

//        JSONArrayIOHandler.loadItems( getFilesDir().getPath() + "/" + getFilename() );
    }

    @Override
    protected void saveItems() throws IOException, ConversionException
    {
        ActionPlansStorage aps = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jc = new JSONArrayConverterActionPlan( items );
        aps.write( jc );
    }

    @Override
    protected void deleteItems( ArrayList<Integer> deleteIndex )
    {
        for( int i = items.size() ; i >= 0  ; i-- )
        {
            if( deleteIndex.contains( i ) )
            {
                items.remove( i );
            }
        }
    }

    @Override
    protected void putItemInIntent( Intent intent, int itemIndex )
    {
        intent.putExtra( "action_plan", items.get( itemIndex ) );
    }

}
