package ahat.mmsnap;

import android.content.Intent;
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
    @Override protected Class<?> getListActivityClass() { return evaluationMode ? DailyEvaluationsListActivity.class : CopingPlansActivity.class; }

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

    public void onBackPressed()
    {
        if( evaluationMode )
        {
            startActivity( new Intent( this, DailyEvaluationsListActivity.class ) );
        }
        else
        {
            startActivity( new Intent( this, CopingPlansActivity.class ) );
        }
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

    @Override
    protected void saveItem() throws IOException, JSONException, ConversionException
    {
        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan();
        s.read( jc );

        if( -1 == item.id )
        {
            item.id =  jc.getCopingPlans().size();
        }

        if( item.id < jc.getCopingPlans().size() )
        {
            jc.getCopingPlans().set( item.id, item );
        }
        else if( item.id == jc.getCopingPlans().size() )
        {
            jc.getCopingPlans().add( item );
        }

        s.write( jc );
    }
}
