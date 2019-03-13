package ahat.mmsnap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.IfThenPlan;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;


public class ActionPlansListAdapter extends IfThenListAdapter
{

    private final ArrayList<ActionPlan> items;

    public ActionPlansListAdapter( Activity context, ArrayList<ActionPlan> items, boolean deleteAction )
    {
        super( context,  deleteAction );
        this.items = items;
    }


    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view

        if( null == view )
        {
//            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            view = inflater.inflate( R.layout.action_plans_list_item, null,true );
        }
//        View rowView = inflater.inflate( R.layout.counterfactual_list_item, null,true );

        //this code gets references to objects in the counterfactual_list_item.xml file
        TextView ifStatement = view.findViewById( R.id.item_if_statement );
        TextView thenStatement = view.findViewById( R.id.item_then_statement );
        TextView copingIfStatement = view.findViewById( R.id.item_coping_if_statement );
        TextView copingThenStatement = view.findViewById( R.id.item_coping_then_statement );
        ImageView active = view.findViewById( R.id.counterfactual_list_item_active );
        ImageView inactive = view.findViewById( R.id.counterfactual_list_item_inactive );
        TextView date = view.findViewById( R.id.date );
        ImageView eating = view.findViewById( R.id.eating_image );
        ImageView activity = view.findViewById( R.id.activity_image );
        ImageView alcohol = view.findViewById( R.id.alcohol_image );
        ImageView smoking = view.findViewById( R.id.smoking_image );

        //this code sets the values of the objects to values from the arrays
        ActionPlan item = items.get( position );

        ifStatement.setText( item.ifStatement );

        thenStatement.setText( item.thenStatement );

         active.setVisibility( item.active ? View.VISIBLE : View.GONE );
        inactive.setVisibility( item.active ? View.GONE: View.VISIBLE );

        ImageView chk = view.findViewById( R.id.counterfactual_list_item_chk );
        chk.setVisibility( deleteAction ? View.VISIBLE : View.GONE );
        chk.setImageResource( deleteIndex.contains( position ) ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp );

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

        if( item.copingIfStatement.trim().length() == 0 && item.copingThenStatement.trim().length() == 0 )
        {
            view.findViewById( R.id.coping_plan_layout ).setVisibility( View.GONE );
        }
        else
        {
            copingIfStatement.setText(  item.copingIfStatement.trim() );
            copingThenStatement.setText(  item.copingThenStatement.trim() );
            view.findViewById( R.id.coping_plan_layout ).setVisibility( View.VISIBLE );
        }

        view.findViewById( R.id.mon_layout ).setVisibility( item.days.contains( IfThenPlan.Day.MONDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.tue_layout ).setVisibility( item.days.contains( IfThenPlan.Day.TUESDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.wed_layout ).setVisibility( item.days.contains( IfThenPlan.Day.WEDNESDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.thu_layout ).setVisibility( item.days.contains( IfThenPlan.Day.THURSDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.fri_layout ).setVisibility( item.days.contains( IfThenPlan.Day.FRIDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sat_layout ).setVisibility( item.days.contains( IfThenPlan.Day.SATURDAY ) ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.sun_layout ).setVisibility( item.days.contains( IfThenPlan.Day.SUNDAY ) ? View.VISIBLE : View.GONE );

        if( item.isEvaluated() )
        {
            view.findViewById( R.id.day_mon_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.MONDAY ) && item.isSuccessful( IfThenPlan.Day.MONDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_tue_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.TUESDAY ) && item.isSuccessful( IfThenPlan.Day.TUESDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_wed_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.WEDNESDAY ) && item.isSuccessful( IfThenPlan.Day.WEDNESDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_thu_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.THURSDAY ) && item.isSuccessful( IfThenPlan.Day.THURSDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_fri_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.FRIDAY ) && item.isSuccessful( IfThenPlan.Day.FRIDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_sat_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SATURDAY ) && item.isSuccessful( IfThenPlan.Day.SATURDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_sun_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SUNDAY ) && item.isSuccessful( IfThenPlan.Day.SUNDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_mon_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.MONDAY ) && !item.isSuccessful( IfThenPlan.Day.MONDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_tue_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.TUESDAY ) && !item.isSuccessful( IfThenPlan.Day.TUESDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_wed_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.WEDNESDAY ) && !item.isSuccessful( IfThenPlan.Day.WEDNESDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_thu_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.THURSDAY ) && !item.isSuccessful( IfThenPlan.Day.THURSDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_fri_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.FRIDAY ) && !item.isSuccessful( IfThenPlan.Day.FRIDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_sat_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SATURDAY ) && !item.isSuccessful( IfThenPlan.Day.SATURDAY ) ? View.VISIBLE : View.GONE );
            view.findViewById( R.id.day_sun_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SUNDAY ) && !item.isSuccessful( IfThenPlan.Day.SUNDAY ) ? View.VISIBLE : View.GONE );
        }
        return view;

    }

//    public View getViewOld(int position, View view, ViewGroup parent)
//    {
//        LayoutInflater inflater = context.getLayoutInflater();
//
//        // Check if an existing view is being reused, otherwise inflate the view
//
//        if( null == view )
//        {
////            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
//            view = inflater.inflate( R.layout.action_plans_list_item, null,true );
//        }
////        View rowView = inflater.inflate( R.layout.counterfactual_list_item, null,true );
//
//        //this code gets references to objects in the counterfactual_list_item.xml file
//        TextView ifStatement = view.findViewById( R.id.item_if_statement );
//        TextView thenStatement = view.findViewById( R.id.item_then_statement );
//        TextView copingIfStatement = view.findViewById( R.id.item_coping_if_statement );
//        TextView copingThenStatement = view.findViewById( R.id.item_coping_then_statement );
//        ImageView active = view.findViewById( R.id.counterfactual_list_item_active );
//        ImageView inactive = view.findViewById( R.id.counterfactual_list_item_inactive );
//        TextView date = view.findViewById( R.id.date );
//        ImageView eating = view.findViewById( R.id.eating_image );
//        ImageView activity = view.findViewById( R.id.activity_image );
//        ImageView alcohol = view.findViewById( R.id.alcohol_image );
//        ImageView smoking = view.findViewById( R.id.smoking_image );
//
//        //this code sets the values of the objects to values from the arrays
//        try
//        {
//            JSONObject item = ( JSONObject) items.get( position );
//
//            ifStatement.setText( item.getString( "if" ) );
//
//            thenStatement.setText( item.getString( "then" ) );
//
//            Calendar calendar = IfThenDetailActivity.getCalendarFromYYYYMMDD( item.getString( "date" )  );
//            DateFormatSymbols dfs = new DateFormatSymbols();
//            date.setText( dfs.getShortWeekdays()[ calendar.get( Calendar.DAY_OF_WEEK ) ]+ " " + calendar.get( Calendar.DAY_OF_MONTH ) + " " +
//                           dfs.getMonths()[ calendar.get( Calendar.MONTH ) ] + " " + calendar.get( Calendar.YEAR ));
//
//            active.setVisibility( item.getBoolean( "active" ) ? View.VISIBLE : View.GONE );
//            inactive.setVisibility( item.getBoolean( "active" ) ? View.GONE: View.VISIBLE );
//
//            ImageView chk = view.findViewById( R.id.counterfactual_list_item_chk );
//            chk.setVisibility( deleteAction ? View.VISIBLE : View.GONE );
//            chk.setImageResource( deleteIndex.contains( position ) ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp );
//
//            eating.setVisibility( item.getBoolean( "DIET" ) ? View.VISIBLE: View.GONE );
//            activity.setVisibility( item.getBoolean( "ACTIVITY" ) ? View.VISIBLE: View.GONE );
//            alcohol.setVisibility( item.getBoolean( "ALCOHOL" ) ? View.VISIBLE: View.GONE );
//            smoking.setVisibility( item.getBoolean( "SMOKING" ) ? View.VISIBLE: View.GONE );
//
//            if( item.getString( "coping_if" ).trim().length() == 0 && item.getString( "coping_then" ).trim().length() == 0 )
//            {
//                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.GONE );
//            }
//            else
//            {
//                copingIfStatement.setText(  item.getString( "coping_if" ).trim() );
//                copingThenStatement.setText(  item.getString( "coping_then" ).trim() );
//                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.VISIBLE );
//            }
//        }
//        catch( Exception e )
//        {
//            e.printStackTrace();
//        }
//
//        return view;
//
//    }

}
