package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;

public class CopingPlansActivity extends IfThenListActivity
{

//    public final String FILENAME = "coping_plans.json";

    protected ArrayList<CopingPlan> items = new ArrayList<CopingPlan>();

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    protected IfThenListAdapter createListAdapter()
    {
        return new IfThenListAdapter( this, items, delete );
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
    protected void loadItems() throws IOException, JSONException, ConversionException
    {
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan();
        s.read( jc );
        items = jc.getCopingPlans();
    }

    @Override
    protected void saveItems() throws IOException, JSONException, ConversionException
    {
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan( items );
        s.write( jc );
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
        intent.putExtra( "coping_plan", items.get( itemIndex ) );
    }

}
