package ahat.mmsnap;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.IfThenPlan;

public class TodaysListAdapter extends ArrayAdapter
{
    @NonNull
    private final Activity context;
    private final ArrayList<IfThenPlan> items;

    public TodaysListAdapter( @NonNull Activity context, ArrayList<IfThenPlan> items )
    {
        super( context, R.layout.todays_list_item );
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public View getView( int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        view = inflater.inflate( R.layout.todays_list_item, null,true );

        TextView textView = view.findViewById( R.id.todays_list_item_text );

        //this code sets the values of the objects to values from the arrays

        try
        {
            IfThenPlan item = items.get( position );
            String text = "<strong>IF</strong>&nbsp;" + item.ifStatement + "&nbsp;<strong>THEN</strong>&nbsp;" + item.thenStatement + ".";
            if( item instanceof ActionPlan &&
                ( ( (ActionPlan) item ).copingIfStatement.trim().length() != 0 || ( (ActionPlan) item ).copingThenStatement.trim().length() != 0 )
            )
            {
                text += "<br>Also, <strong>IF</strong>&nbsp; " + ( (ActionPlan) item ).copingIfStatement.trim() + "&nbsp;<strong>THEN</strong>&nbsp;" +
                        ( (ActionPlan) item ).copingThenStatement.trim() + ".";
            }

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                textView.setText( Html.fromHtml( text, Html.FROM_HTML_MODE_COMPACT ) );
            }
            else
            {
                textView.setText( Html.fromHtml( text ) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };
}
