package ahat.mmsnap;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import ahat.mmsnap.models.DailyEvaluation;
import ahat.mmsnap.models.IfThenPlan;

import static ahat.mmsnap.models.IfThenPlan.WeekDay.FRIDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.WEDNESDAY;

public class DailyEvaluationListAdapter extends IfThenListAdapter
{
    private final Activity                   context;   //to reference the Activity
    public        ArrayList<DailyEvaluation> items;

    DailyEvaluationListAdapter( Activity context, ArrayList<DailyEvaluation> items, boolean menuAction )
    {
        super( context );

        this.context = context;
        this.items   = items;
    }

    @Override
    public int getCount(){ return items.size(); }

    @Override
    public View getView( int position, View view, ViewGroup parent)
    {

        view = super.getView( position, view, parent );

        DailyEvaluation item = items.get( position );

        view.findViewById( R.id.counterfactual_list_item_active ).setVisibility( View.GONE );
        view.findViewById( R.id.counterfactual_list_item_inactive ).setVisibility( View.GONE );
        view.findViewById( R.id.counterfactual_list_item_expired ).setVisibility( View.GONE );
        view.findViewById( R.id.pending_img ).setVisibility( !item.isEvaluated() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.success_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );

        view.findViewById( R.id.day_mon_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_tue_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_wed_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_thu_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_fri_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_sat_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_sun_check_img ).setVisibility( item.isEvaluated() && item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_mon_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_tue_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_wed_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_thu_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_fri_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_sat_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.day_sun_fail_img ).setVisibility( item.isEvaluated() && !item.isSuccessful() ? View.VISIBLE : View.GONE );

        Drawable highlightBkg = view.getResources().getDrawable( R.drawable.custom_radio_highglight, null );
        if( item.getWeekDay() == MONDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_mon_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == TUESDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_tue_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == WEDNESDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_wed_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == THURSDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_thu_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == FRIDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_fri_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == SATURDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_sat_chk ).setBackground( highlightBkg  ); }
        if( item.getWeekDay() == SUNDAY && item.dayHasPassed() && !item.isEvaluated() ) { view.findViewById( R.id.day_sun_chk ).setBackground( highlightBkg  ); }

        view.findViewById( R.id.mon_layout ).setVisibility( item.getWeekDay() == MONDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.tue_layout ).setVisibility( item.getWeekDay() == TUESDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.wed_layout ).setVisibility( item.getWeekDay() == WEDNESDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.thu_layout ).setVisibility( item.getWeekDay() == THURSDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.fri_layout ).setVisibility( item.getWeekDay() == FRIDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sat_layout ).setVisibility( item.getWeekDay() == SATURDAY ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sun_layout ).setVisibility( item.getWeekDay() == SUNDAY ? View.VISIBLE : View.GONE );


        //setup the date
        view.findViewById( R.id.dateSpacer ).setVisibility( View.GONE );
        view.findViewById( R.id.endDate ).setVisibility( View.GONE );
        view.findViewById( R.id.week_days_layout ).setVisibility( View.GONE );

        TextView dateTextView = view.findViewById( R.id.startdate );
        dateTextView.setVisibility( View.VISIBLE );

        DateFormatSymbols dfs = new DateFormatSymbols();

        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, item.plan.year );
        cal.set( Calendar.WEEK_OF_YEAR, item.plan.weekOfYear );
        cal.set( Calendar.DAY_OF_WEEK, item.getWeekDay().toCalendarDayOfWeek() );

        dateTextView.setText( dfs.getShortWeekdays()[ cal.get( Calendar.DAY_OF_WEEK ) ]+ " " + cal.get( Calendar.DAY_OF_MONTH ) + " " +
                              dfs.getShortMonths()[ cal.get( Calendar.MONTH ) ] + " " + cal.get( Calendar.YEAR ) );

        return view;
    }

    @Override
    protected IfThenPlan getIfThenPlan( int position )
    {
        return items.get( position ).plan;
    }

}
