package ahat.mmsnap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CopingPlansListAdapter extends IfThenListAdapter
{

    public CopingPlansListAdapter( Activity context, JSONArray items, boolean deleteAction )
    {
        super( context, items, deleteAction );
    }


    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view

        if( null == view )
        {
//            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            view = inflater.inflate( R.layout.coping_plans_list_item, null,true );
        }
//        View rowView = inflater.inflate( R.layout.counterfactual_list_item, null,true );

        //this code gets references to objects in the counterfactual_list_item.xml file
        TextView ifStatement = view.findViewById( R.id.item_if_statement );
        TextView thenStatement = view.findViewById( R.id.item_then_statement );
        ImageView active = view.findViewById( R.id.counterfactual_list_item_active );
        ImageView inactive = view.findViewById( R.id.counterfactual_list_item_inactive );
        TextView date = view.findViewById( R.id.date );
        ImageView eating = view.findViewById( R.id.eating_image );
        ImageView activity = view.findViewById( R.id.activity_image );
        ImageView alcohol = view.findViewById( R.id.alcohol_image );
        ImageView smoking = view.findViewById( R.id.smoking_image );

        //this code sets the values of the objects to values from the arrays
        try
        {
            JSONObject item = ( JSONObject) items.get( position );

            ifStatement.setText( item.getString( "if" ) );

            thenStatement.setText( item.getString( "then" ) );

            Calendar calendar = IfThenDetailActivity.getCalendarFromYYYYMMDD( item.getString( "date" )  );
            DateFormatSymbols dfs = new DateFormatSymbols();
            date.setText( dfs.getShortWeekdays()[ calendar.get( Calendar.DAY_OF_WEEK ) ]+ " " + calendar.get( Calendar.DAY_OF_MONTH ) + " " +
                          dfs.getMonths()[ calendar.get( Calendar.MONTH ) ] + " " + calendar.get( Calendar.YEAR ));

            active.setVisibility( item.getBoolean( "active" ) ? View.VISIBLE : View.GONE );
            inactive.setVisibility( item.getBoolean( "active" ) ? View.GONE: View.VISIBLE );

            ImageView chk = view.findViewById( R.id.counterfactual_list_item_chk );
            chk.setVisibility( deleteAction ? View.VISIBLE : View.GONE );
            chk.setImageResource( deleteIndex.contains( position ) ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp );

            eating.setVisibility( item.getBoolean( "EATING" ) ? View.VISIBLE: View.GONE );
            activity.setVisibility( item.getBoolean( "ACTIVITY" ) ? View.VISIBLE: View.GONE );
            alcohol.setVisibility( item.getBoolean( "ALCOHOL" ) ? View.VISIBLE: View.GONE );
            smoking.setVisibility( item.getBoolean( "SMOKING" ) ? View.VISIBLE: View.GONE );

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };

}
