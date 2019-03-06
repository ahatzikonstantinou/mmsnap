package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActionPlansDetailActivity extends IfThenDetailActivity //AppCompatActivity
{

    @Override
    protected int getActivityResLayout()
    {
        return R.layout.activity_action_plans_detail;
    }
    protected int getContentRootLayoutResId()
    {
        return R.id.action_plans_root_layout;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getSupportActionBar().setSubtitle( R.string.title_activity_action_plans );


    }



    protected JSONObject getIfThenItem()
    {
        JSONObject item = new JSONObject();

        try
        {
            JSONArray items = JSONArrayIOHandler.loadItems( this,
                                                            findViewById( getContentRootLayoutResId() ),
                                                            "Could not load action plans.",
                                                            getFilesDir().getPath() + "/" + getFILENAME() );

            int itemId = getItemId();
            if( -1 == itemId )
            {
                itemId = items.length();
                item.put( "id", String.valueOf( itemId ) );
                item.put( "if", "" );
                item.put( "then", "" );
                item.put( "active", true );
            }
            else if( itemId < items.length() )
            {
                item = (JSONObject) items.get( itemId );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( getContentRootLayoutResId() ), "Could not get if then item", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }
        return item;
    }
}
