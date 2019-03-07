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
import java.util.Calendar;
import java.util.Date;


public class ActionPlansListAdapter extends IfThenListAdapter
{

    public ActionPlansListAdapter( Activity context, JSONArray items, boolean deleteAction )
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
        try
        {
            JSONObject item = ( JSONObject) items.get( position );

            ifStatement.setText( item.getString( "if" ) );

            thenStatement.setText( item.getString( "then" ) );

            DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd");
            final Calendar calendar = Calendar.getInstance();
            try
            {
                final Date itemDate = dateFormat.parse( item.getString( "date" ) );
                calendar.setTime( itemDate );
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

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

            if( item.getString( "coping_if" ).trim().length() == 0 && item.getString( "coping_then" ).trim().length() == 0 )
            {
                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.GONE );
            }
            else
            {
                copingIfStatement.setText(  item.getString( "coping_if" ).trim() );
                copingThenStatement.setText(  item.getString( "coping_then" ).trim() );
                view.findViewById( R.id.coping_plan_layout ).setVisibility( View.VISIBLE );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };

}
