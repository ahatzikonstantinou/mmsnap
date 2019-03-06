package ahat.mmsnap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class IfThenListAdapter extends ArrayAdapter
{
    protected final Activity context;   //to reference the Activity
    protected final JSONArray items;
    public          boolean   deleteAction;
    public          ArrayList<Integer> deleteIndex = new ArrayList();

    public IfThenListAdapter( Activity context, JSONArray items, boolean deleteAction )
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
    public abstract View getView(int position, View view, ViewGroup parent);

}
