package ahat.mmsnap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.models.Reminder;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.FRIDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.WEDNESDAY;

public class IfThenListAdapter extends ArrayAdapter
{
    protected final Activity                        context;   //to reference the Activity
    protected final ArrayList<? extends IfThenPlan> items;
    public          boolean                         menuAction;
    public          ArrayList<Integer>              menuActionItemIndex = new ArrayList();

    public IfThenListAdapter( Activity context, ArrayList<? extends IfThenPlan> items, boolean menuAction )
    {
        super( context, R.layout.action_plans_list_item );

        this.context = context;
        this.items   = items;
        this.menuAction = menuAction;
    }

    public IfThenListAdapter( Activity context )
    {
        super( context, R.layout.action_plans_list_item );

        this.context = context;
        this.items   = new ArrayList<>(0);
        this.menuAction = false;
    }

    @Override
    public int getCount(){ return items.size(); }

    @Override
    public View getView( int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view

        //ahat: NOTE. DO NOT use the following code because it introduces a weird bug. The list of items includes action plans and coping plans.
        //It is expected that the list will include action plans that have the same id as some coping plans. In such a case, as soon as
        //the coping plan is rendered (e.g. scrolled into view in case of a long list), the corresponding action plan will have its
        //similar days rendered with the same highlighted background. E.g. action plan id:8 non-evaluated days MONDAY SUNDAY but SUNDAY is in the future
        //so it should not be highligthed. coping plan id:8 non-evaluated days MONDAY SUNDAY but both should be evaluated. As soon as coping plan id:8
        //is rendered , SUNDAY in action plan id:8 will also be (wrongly) highlighted. While coping plan id:8 is not rendered (is outside of the screen
        //because this is a long list with many items) action plan id:8 is rendered correctly.
        //To fix, inflate the view EVERY time, not just when it is null
//        if( null == view )
//        {
//            view = inflater.inflate( R.layout.action_plans_list_item, null,true );
//        }
        view = inflater.inflate( R.layout.action_plans_list_item, null,true );

        //this code gets references to objects in the counterfactual_list_item.xml file
        TextView ifStatement = view.findViewById( R.id.item_if_statement );
        TextView thenStatement = view.findViewById( R.id.item_then_statement );
        ImageView active = view.findViewById( R.id.counterfactual_list_item_active );
        ImageView inactive = view.findViewById( R.id.counterfactual_list_item_inactive );
        ImageView expiredIcon = view.findViewById( R.id.counterfactual_list_item_expired );
        ImageView eating = view.findViewById( R.id.eating_image );
        ImageView activity = view.findViewById( R.id.activity_image );
        ImageView alcohol = view.findViewById( R.id.alcohol_image );
        ImageView smoking = view.findViewById( R.id.smoking_image );

        //this code sets the values of the objects to values from the arrays
        IfThenPlan item = getIfThenPlan( position );

        ifStatement.setText( item.ifStatement );

        thenStatement.setText( item.thenStatement );

        Calendar expCal = Calendar.getInstance();
        boolean expired = item.year < expCal.get( Calendar.YEAR ) || ( item.year == expCal.get( Calendar.YEAR ) && item.weekOfYear < expCal.get( Calendar.WEEK_OF_YEAR ) );
        active.setVisibility( !expired && item.active ? View.VISIBLE : View.GONE );
        inactive.setVisibility( !expired && item.active ? View.GONE: View.VISIBLE );
        expiredIcon.setVisibility( expired ? View.VISIBLE : View.GONE );

        ImageView chk = view.findViewById( R.id.counterfactual_list_item_chk );
        chk.setVisibility( menuAction ? View.VISIBLE : View.GONE );
        chk.setImageResource( menuActionItemIndex.contains( position ) ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp );

        eating.setVisibility( item.isTarget( DIET ) ? View.VISIBLE : View.GONE );
        activity.setVisibility( item.isTarget( ACTIVITY ) ? View.VISIBLE: View.GONE );
        alcohol.setVisibility( item.isTarget( ALCOHOL ) ? View.VISIBLE: View.GONE );
        smoking.setVisibility( item.isTarget( SMOKING ) ? View.VISIBLE: View.GONE );

        DateFormatSymbols dfs = new DateFormatSymbols();

        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, item.year );
        startCal.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, startCal.get( Calendar.YEAR ) );
        c.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) );
        c.set( Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() );   // go to the first day of this week

        TextView start = view.findViewById( R.id.startdate );
        start.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getShortMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

        c.add( Calendar.DATE, 6);
        TextView end = view.findViewById( R.id.endDate );
        end.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                     dfs.getShortMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

        view.findViewById( R.id.mon_layout ).setVisibility( item.hasDay( MONDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.tue_layout ).setVisibility( item.hasDay( TUESDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.wed_layout ).setVisibility( item.hasDay( WEDNESDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.thu_layout ).setVisibility( item.hasDay( THURSDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.fri_layout ).setVisibility( item.hasDay( FRIDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sat_layout ).setVisibility( item.hasDay( SATURDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sun_layout ).setVisibility( item.hasDay( SUNDAY ) ? View.VISIBLE : View.GONE );

//        if( item.isEvaluated() )
//        {
//            view.findViewById( R.id.day_mon_check_img ).setVisibility( item.isEvaluated( MONDAY ) && item.isSuccessful( MONDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_tue_check_img ).setVisibility( item.isEvaluated( TUESDAY ) && item.isSuccessful( TUESDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_wed_check_img ).setVisibility( item.isEvaluated( WEDNESDAY ) && item.isSuccessful( WEDNESDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_thu_check_img ).setVisibility( item.isEvaluated( THURSDAY ) && item.isSuccessful( THURSDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_fri_check_img ).setVisibility( item.isEvaluated( FRIDAY ) && item.isSuccessful( FRIDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_sat_check_img ).setVisibility( item.isEvaluated( SATURDAY ) && item.isSuccessful( SATURDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_sun_check_img ).setVisibility( item.isEvaluated( SUNDAY ) && item.isSuccessful( SUNDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_mon_fail_img ).setVisibility( item.isEvaluated( MONDAY ) && !item.isSuccessful( MONDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_tue_fail_img ).setVisibility( item.isEvaluated( TUESDAY ) && !item.isSuccessful( TUESDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_wed_fail_img ).setVisibility( item.isEvaluated( WEDNESDAY ) && !item.isSuccessful( WEDNESDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_thu_fail_img ).setVisibility( item.isEvaluated( THURSDAY ) && !item.isSuccessful( THURSDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_fri_fail_img ).setVisibility( item.isEvaluated( FRIDAY ) && !item.isSuccessful( FRIDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_sat_fail_img ).setVisibility( item.isEvaluated( SATURDAY ) && !item.isSuccessful( SATURDAY ) ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.day_sun_fail_img ).setVisibility( item.isEvaluated( SUNDAY ) && !item.isSuccessful( SUNDAY ) ? View.VISIBLE : View.GONE );
//        }
//
//        Drawable highlightBkg = view.getResources().getDrawable( R.drawable.custom_radio_highglight, null );
//        if( item.hasDay( MONDAY ) && item.dayHasPassed( MONDAY ) && !item.isEvaluated( MONDAY ) ) { view.findViewById( R.id.day_mon_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( TUESDAY ) && item.dayHasPassed( TUESDAY ) && !item.isEvaluated( TUESDAY ) ) { view.findViewById( R.id.day_tue_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( WEDNESDAY ) && item.dayHasPassed( WEDNESDAY ) && !item.isEvaluated( WEDNESDAY ) ) { view.findViewById( R.id.day_wed_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( THURSDAY ) && item.dayHasPassed( THURSDAY ) && !item.isEvaluated( THURSDAY ) ) { view.findViewById( R.id.day_thu_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( FRIDAY ) && item.dayHasPassed( FRIDAY ) && !item.isEvaluated( FRIDAY ) ) { view.findViewById( R.id.day_fri_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( SATURDAY ) && item.dayHasPassed( SATURDAY ) && !item.isEvaluated( SATURDAY ) ) { view.findViewById( R.id.day_sat_chk ).setBackground( highlightBkg  ); }
//        if( item.hasDay( SUNDAY ) && item.dayHasPassed( SUNDAY ) && !item.isEvaluated( SUNDAY ) ) { view.findViewById( R.id.day_sun_chk ).setBackground( highlightBkg  ); }

        if( item instanceof ActionPlan )
        {
            TextView copingIfStatement = view.findViewById( R.id.item_coping_if_statement );
            TextView copingThenStatement = view.findViewById( R.id.item_coping_then_statement );
            ActionPlan apItem = (ActionPlan) item;

            if( apItem.copingIfStatement.trim().length() == 0 && apItem.copingThenStatement.trim().length() == 0 )
            {
                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.GONE );
            }
            else
            {
                copingIfStatement.setText( apItem.copingIfStatement.trim() );
                copingThenStatement.setText( apItem.copingThenStatement.trim() );
                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.VISIBLE );
            }
        }
        else
        {
            view.findViewById( R.id.coping_plan_layout ).setVisibility( View.GONE );
        }

        if( 0 == item.reminders.size() )
        {
            view.findViewById( R.id.reminders_layout ).setVisibility( View.GONE );
        }
        else
        {
            Collections.sort( item.reminders, Reminder.comparator );
            FlexboxLayout reminderLayout = view.findViewById( R.id.reminder_layout );

            for( Reminder reminder : item.reminders )
            {
//                LinearLayout timeLayout = new LinearLayout( view.getContext() );
//                timeLayout.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
//                timeLayout.setOrientation( LinearLayout.HORIZONTAL );
//                timeLayout.setGravity( Gravity.CENTER_VERTICAL );
//                setMargins( timeLayout, 4, 4, 4, 4 );
//                setPadding( timeLayout, 4, 4, 4, 4 );

                TextView time = new TextView( view.getContext() );
                time.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
                setMargins( time, 0, 0, 4, 0 );
                setPadding( time, 2, 2, 2, 2 );
                time.setText( ( reminder.hour < 10 ? "0": "" ) + String.valueOf( reminder.hour ) + ":" + ( reminder.minute < 10 ? "0" : "" ) + String.valueOf( reminder.minute ) );
                reminderLayout.addView( time );
            }
        }

        return view;
    }

    //set margins in dp
    private void setMargins( View view, int left, int top, int right, int bottom )
    {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = view.getContext().getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    //set padding in dp
    private void setPadding( View view, int left, int top, int right, int bottom )
    {
        final float scale = view.getContext().getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int l =  (int)(left * scale + 0.5f);
        int r =  (int)(right * scale + 0.5f);
        int t =  (int)(top * scale + 0.5f);
        int b =  (int)(bottom * scale + 0.5f);

        view.setPadding(l, t, r, b);
        view.requestLayout();
    }

    protected IfThenPlan getIfThenPlan( int position )
    {
        return items.get( position );
    }

}
