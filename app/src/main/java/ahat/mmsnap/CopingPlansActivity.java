package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.CopingPlan;
import ahat.mmsnap.models.IfThenPlan;

public class CopingPlansActivity extends IfThenListActivity
{

//    public final String FILENAME = "coping_plans.json";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    protected IfThenListAdapter createListAdapter()
    {
        return new IfThenListAdapter( this, items, selectItemsMode );
    }

//    protected String getFilename()
//    {
//        return FILENAME;
//    }

    protected String getLoadItemsErrorMessage()
    {
        return "Coping plans could not be loaded.";
    }

    protected String getDeleteItemsErrorMessage()
    {
        return "Could not delete selected coping plans";
    }

    protected int getSubtitleStringResId()
    {
        return R.string.title_activity_coping_plans;
    }

    protected int getLogoDrawableResId()
    {
        return R.drawable.if_then_section_logo;
    }

    protected Class<?> getDetailActivityClass()
    {
        return CopingPlansDetailActivity.class;
    }

    @Override
    protected ArrayList<IfThenPlan> loadItems() throws IOException, JSONException, ConversionException
    {
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan();
        s.read( jc );
        ArrayList<CopingPlan> items = jc.getCopingPlans();

        ArrayList<IfThenPlan> returnArray = new ArrayList<>( items.size() );
        for( CopingPlan p : items )
        {
            returnArray.add( p );
        }
        return returnArray;
    }

    @Override
    protected void saveItems() throws IOException, JSONException, ConversionException
    {
        ArrayList<CopingPlan> plans = new ArrayList<>( items.size() );
        for( IfThenPlan p : items )
        {
            plans.add( (CopingPlan) p );
        }
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan( plans );
        s.write( jc );
    }


    @Override
    protected void putItemInIntent( Intent intent, int itemIndex )
    {
        intent.putExtra( "coping_plan", items.get( itemIndex ) );
    }

}
