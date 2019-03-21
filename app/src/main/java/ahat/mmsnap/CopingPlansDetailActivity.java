package ahat.mmsnap;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;
import ahat.mmsnap.Models.Reminder;
import ahat.mmsnap.Notifications.ReminderAlarmReceiver;

public class CopingPlansDetailActivity extends IfThenDetailActivity //AppCompatActivity
{

    @Override
    protected int getActivityResLayout()
    {
        return R.layout.activity_action_plans_detail;
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

        // fix color and title for the layout
        LinearLayout borderLayout = findViewById( R.id.action_plan_border_layout );
        borderLayout.getBackground().setColorFilter( getResources().getColor( R.color.coping_plan ), PorterDuff.Mode.SRC_ATOP );
        TextView title = findViewById( R.id.action_plan_title_textView );
        title.getBackground().setColorFilter( getResources().getColor( R.color.coping_plan ), PorterDuff.Mode.SRC_ATOP );
        title.setText( "COPING PLAN" );

        // hide the coping plan frame which is only used in action plans
        findViewById( R.id.coping_plan_container_layout ).setVisibility( View.GONE );
    }

    public void onBackPressed()
    {
        startActivity( new Intent( this, CopingPlansActivity.class ) );
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
    protected void saveItem( ArrayList<IfThenPlan.WeekDay> days, ArrayList<Reminder> reminders ) throws IOException, JSONException, ConversionException
    {
        // cancel previous reminders
        for( IfThenPlan.WeekDay day : item.days )
        {
            for( Reminder reminder : item.reminders )
            {
                ReminderAlarmReceiver.cancelAlarm( this, item.year, item.weekOfYear, day, reminder.hour, reminder.minute );
            }
        }

        // start new reminders
        for( IfThenPlan.WeekDay day : days )
        {
            for( Reminder reminder : reminders )
            {
                ReminderAlarmReceiver.setupAlarm( this, item.year, item.weekOfYear, day, reminder.hour, reminder.minute );
            }
        }

        item.days = days;
        item.reminders = reminders;

        CopingPlansStorage s = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jc = new JSONArrayConverterCopingPlan();
        s.read( jc );

        if( -1 == item.id )
        {
            item.id =  jc.getCopingPlans().size();
        }

        if( item.id < jc.getCopingPlans().size() )
        {
            int i = 0;
            for( ; i < jc.getCopingPlans().size() ; i++ )
            {
                if( jc.getCopingPlans().get( i ).id == item.id )
                {
                    jc.getCopingPlans().set( i, item );
                    break;
                }
            }
            if( i == jc.getCopingPlans().size() )
            {
                throw new ConversionException( new Exception( "Error saving coping plan. Coping plan with id " + String.valueOf( item.id ) + " not found in existing list." ) );
            }
        }
        else if( item.id == jc.getCopingPlans().size() )
        {
            jc.getCopingPlans().add( item );
        }

        s.write( jc );
    }
}
