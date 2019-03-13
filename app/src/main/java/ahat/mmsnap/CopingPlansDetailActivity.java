package ahat.mmsnap;

import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;

import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;

public class CopingPlansDetailActivity extends IfThenDetailActivity //AppCompatActivity
{

    @Override
    protected int getActivityResLayout()
    {
        return R.layout.activity_coping_plans_detail;
    }
    @Override
    protected int getContentRootLayoutResId()
    {
        return R.id.coping_plans_root_layout;
    }
    @Override protected Class<?> getListActivityClass() { return CopingPlansActivity.class; }

    @Override
    protected String getSaveErrorMessage()
    {
        return "Could not save coping plan";
    }

    private CopingPlan item;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getSupportActionBar().setSubtitle( R.string.title_activity_coping_plans );
    }

    @Override
    protected IfThenPlan getIfThenItem()
    {
        item = CopingPlan.createNew();
        if( getIntent().hasExtra( "coping_plan" ) )
        {
            item = (CopingPlan) getIntent().getSerializableExtra( "coping_plan" );
        }

        return item;
    }

//    protected JSONObject getIfThenItem()
//    {
//        JSONObject item = new JSONObject();
//
//        try
//        {
//            JSONArray items = JSONArrayIOHandler.loadItems( getFilesDir().getPath() + "/" + getFILENAME() );
//
//            int itemId = getItemId();
//            if( -1 == itemId )
//            {
//                itemId = items.length();
//                item.put( "id", String.valueOf( itemId ) );
//                item.put( "if", "My friends ask me to go out for beers" );
//                item.put( "then", "I will only have non-alcoholic drinks" );
//                item.put( "active", true );
//                item.put( "date", "" );
//                item.put( "DIET", false );
//                item.put( "ACTIVITY", false );
//                item.put( "ALCOHOL", false );
//                item.put( "SMOKING", false );
//
//            }
//            else if( itemId < items.length() )
//            {
//                item = (JSONObject) items.get( itemId );
//            }
//        }
//        catch( Exception e )
//        {
//            e.printStackTrace();
//            Snackbar.make( findViewById( getContentRootLayoutResId() ), "Could not get coping plan", Snackbar.LENGTH_INDEFINITE )
//                    .setAction( "Retry", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            startActivity( getIntent() );
//                        }
//                    } ).show();
//        }
//        return item;
//    }

    @Override
    protected void saveItem() throws IOException, JSONException, ConversionException
    {
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan();
        s.read( jc );

        int itemId = item.id;
        if( -1 == itemId )
        {
            itemId =  jc.getCopingPlans().size();
        }

        if( itemId < jc.getCopingPlans().size() )
        {
            jc.getCopingPlans().set( itemId, item );
        }
        else if( itemId == jc.getCopingPlans().size() )
        {
            jc.getCopingPlans().add( item );
        }

        s.write( jc );
    }
}
