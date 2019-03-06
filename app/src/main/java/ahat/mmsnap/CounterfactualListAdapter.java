package ahat.mmsnap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class CounterfactualListAdapter extends IfThenListAdapter
{

    public CounterfactualListAdapter( Activity context, JSONArray items, boolean deleteAction )
    {
        super( context, items, deleteAction );
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
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return view;

    };

}
