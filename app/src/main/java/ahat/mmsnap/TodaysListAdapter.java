package ahat.mmsnap;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodaysListAdapter extends ArrayAdapter
{
    @NonNull
    private final Activity context;
    private final JSONArray items;

    public TodaysListAdapter( @NonNull Activity context, JSONArray items )
    {
        super( context, R.layout.todays_list_item );
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount()
    {
        return items.length();
    }

    @Override
    public View getView( int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if( null == view )
        {
            view = inflater.inflate( R.layout.todays_list_item, null,true );
        }

        TextView textView = view.findViewById( R.id.todays_list_item_text );

        //this code sets the values of the objects to values from the arrays
        String text = "";
        try
        {
            JSONObject item = ( JSONObject) items.get( position );

            text = "IF " + item.getString( "if" ) +
                   " THEN " + item.getString( "then" ) + ".";

            if( item.getString( "coping_if" ).trim().length() != 0 || item.getString( "coping_then" ).trim().length() != 0 )
            {
                text += " Also, IF " + item.getString( "coping_if" ).trim() +
                        " THEN " + item.getString( "coping_then" ).trim() + ".";
            }
            textView.setText( text );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };
}
