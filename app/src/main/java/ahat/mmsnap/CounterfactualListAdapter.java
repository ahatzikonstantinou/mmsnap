package ahat.mmsnap;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CounterfactualListAdapter extends ArrayAdapter
{
    //to reference the Activity
    private final Activity context;

    //to store the animal images
    private final JSONArray items;
    public boolean deleteAction;
    public ArrayList deleteIndex = new ArrayList();

    public CounterfactualListAdapter( Activity context, JSONArray items, boolean deleteAction )
    {
        super( context, R.layout.counterfactual_list_item );

        this.context = context;
        this.items   = items;
        this.deleteAction = deleteAction;
    }

    @Override
    public int getCount()
    {
        return items.length();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view

//        if( null == view )
//        {
//            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            view = inflater.inflate( R.layout.counterfactual_list_item, null,true );
//        }
//        View rowView = inflater.inflate( R.layout.counterfactual_list_item, null,true );

        //this code gets references to objects in the counterfactual_list_item.xml file
        TextView ifStatement = (TextView) view.findViewById( R.id.counterfactual_item_if_statement );
        TextView thenStatement = (TextView) view.findViewById( R.id.counterfactual_item_then_statement );
        ImageView active = (ImageView) view.findViewById( R.id.counterfactual_list_item_active );
        ImageView inactive = (ImageView) view.findViewById( R.id.counterfactual_list_item_inactive );

        //this code sets the values of the objects to values from the arrays
        try
        {
            JSONObject item = ( JSONObject) items.get( position );
            ifStatement.setText( item.getString( "if" ) );
            thenStatement.setText( item.getString( "then" ) );
            active.setVisibility( item.getBoolean( "active" ) ? View.VISIBLE : View.GONE );
            inactive.setVisibility( item.getBoolean( "active" ) ? View.GONE: View.VISIBLE );

            ImageView chk = (ImageView) view.findViewById( R.id.counterfactual_list_item_chk );
            chk.setVisibility( deleteAction ? View.VISIBLE : View.GONE );
            chk.setImageResource( deleteIndex.contains( position ) ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp );
//            String m = "Item " + String.valueOf( position ) + " is " + ( deleteIndex.contains( position ) ? "" : "not" ) + " in deleteIndex";
//            Log.d( "Ahat:", m );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };

//    public ArrayList getCheckedIndexes()
//    {
//        LayoutInflater inflater = context.getLayoutInflater();
//        View view = inflater.inflate( R.id.counterfactual_list, null,true );
//
//    }

}
