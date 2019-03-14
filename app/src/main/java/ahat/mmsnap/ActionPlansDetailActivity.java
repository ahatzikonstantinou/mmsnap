package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterActionPlan;
import ahat.mmsnap.JSON.JSONConverterActionPlan;
import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

public class ActionPlansDetailActivity extends IfThenDetailActivity //AppCompatActivity
{

    @Override
    protected int getActivityResLayout()
    {
        return R.layout.activity_action_plans_detail;
    }
    @Override
    protected int getContentRootLayoutResId()
    {
        return R.id.action_plans_root_layout;
    }
    @Override protected Class<?> getListActivityClass() { return evaluationMode ? DailyEvaluationsListActivity.class : ActionPlansActivity.class; }

    @Override
    protected String getSaveErrorMessage()
    {
        return "Could not save action plan";
    }

    private TextView copingIfStatementTextView;
    private TextView copingThenStatementTextView;

    private ActionPlan item;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getSupportActionBar().setSubtitle( R.string.title_activity_action_plans );

        copingIfStatementTextView = findViewById( R.id.item_coping_plan_if_statement );
        copingThenStatementTextView = findViewById( R.id.item_coping_plan_then_statement );

        copingIfStatementTextView.setText( item.copingIfStatement );
        copingThenStatementTextView.setText( item.copingThenStatement );

        if( planIsExpired || evaluationMode )
        {
            copingIfStatementTextView.setEnabled( false );
            copingThenStatementTextView.setEnabled( false );
        }
    }

    public void onBackPressed()
    {
        if( evaluationMode )
        {
            startActivity( new Intent( this, DailyEvaluationsListActivity.class ) );
        }
        else
        {
            startActivity( new Intent( this, ActionPlansActivity.class ) );
        }
    }

    protected IfThenPlan getIfThenItem()
    {
        item = ActionPlan.createNew();
        if( getIntent().hasExtra( "action_plan" ) )
        {
            item = (ActionPlan) getIntent().getSerializableExtra( "action_plan" );
        }

        return item;
    }

    @Override
    protected String fillItemFromUI() throws JSONException
    {
        String error = super.fillItemFromUI();

        Calendar now = Calendar.getInstance();
        // if the plan is passed it's week it can only be evaluated
        if( now.get( Calendar.WEEK_OF_YEAR ) != item.weekOfYear || now.get( Calendar.YEAR ) != item.year )
        {
            return error;
        }

        String copingIfStatement = copingIfStatementTextView.getText().toString().trim();
        String copingThenStatement = copingThenStatementTextView.getText().toString().trim();

        if( ( 0 == copingIfStatement.length() && 0 < copingThenStatement.length() ) ||
            ( 0 < copingIfStatement.length() && 0 == copingThenStatement.length() )
        )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "fill either both or none of the coping plan statements";
        }
        if( 0 < error.length() )
        {
            return error;
        }

        item.copingIfStatement = copingIfStatement;
        item.copingThenStatement = copingThenStatement;

        return error;
    }

    @Override
    protected void saveItem() throws IOException, JSONException, ConversionException
    {
        ActionPlansStorage s = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jc = new JSONArrayConverterActionPlan();
        s.read( jc );

        if( -1 == item.id )
        {
            item.id =  jc.getActionPlans().size();
        }

        if( item.id < jc.getActionPlans().size() )
        {
            for( int i = 0 ; i < jc.getActionPlans().size() ; i++ )
            {
                if( jc.getActionPlans().get( i ).id == item.id )
                {
                    jc.getActionPlans().set( item.id, item );
                    break;
                }
                throw new ConversionException( new Exception( "Error saving action plan. Action plan with id " + String.valueOf( item.id ) + " not found in existing list." ) );
            }

        }
        else if( item.id == jc.getActionPlans().size() )
        {
            jc.getActionPlans().add( item );
        }

        s.write( jc );
    }

}
