package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    @Override
    protected int getContentRootLayoutResId()
    {
        return R.id.action_plans_root_layout;
    }
    @Override protected Class<?> getListActivityClass() { return ActionPlansActivity.class; }

    @Override
    protected String getSaveErrorMessage()
    {
        return "Could not save action plan";
    }

    private TextView copingIfStatementTextView;
    private TextView copingThenStatementTextView;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getSupportActionBar().setSubtitle( R.string.title_activity_action_plans );

        copingIfStatementTextView = findViewById( R.id.item_coping_plan_if_statement );
        copingThenStatementTextView = findViewById( R.id.item_coping_plan_then_statement );

        try
        {
            copingIfStatementTextView.setText( item.getString( "coping_if" ) );
            copingThenStatementTextView.setText( item.getString( "coping_then" ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            View view = findViewById( getContentRootLayoutResId() );
            Snackbar.make( view, "Could not parse action plan!", Snackbar.LENGTH_LONG).show();
        }
    }


    protected JSONObject getIfThenItem()
    {
        JSONObject item = new JSONObject();

        try
        {
            JSONArray items = JSONArrayIOHandler.loadItems( getFilesDir().getPath() + "/" + getFILENAME() );

            int itemId = getItemId();
            if( -1 == itemId )
            {
                itemId = items.length();
                item.put( "id", String.valueOf( itemId ) );
                item.put( "if", "I return from work before 8 o'clock" );
                item.put( "then", "I will go to the gym" );
                item.put( "active", true );
                item.put( "date", "" );
                item.put( "DIET", false );
                item.put( "ACTIVITY", false );
                item.put( "ALCOHOL", false );
                item.put( "SMOKING", false );
                item.put( "coping_if", "I am very tired" );
                item.put( "coping_then", "I will go for 40 minutes of brisk walk" );

            }
            else if( itemId < items.length() )
            {
                item = (JSONObject) items.get( itemId );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( getContentRootLayoutResId() ), "Could not get action plan", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }
        return item;
    }

    @Override
    protected String fillItemFromUI() throws JSONException
    {
        String error = super.fillItemFromUI();

        String copingIfStatement = copingIfStatementTextView.getText().toString().trim();
        String copingThenStatement = copingThenStatementTextView.getText().toString().trim();

        if( ( 0 == copingIfStatement.length() && 0 < copingThenStatement.length() ) ||
            ( 0 < copingIfStatement.length() && 0 == copingThenStatement.length() )
        )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "fill either both or none of the coping plan statements";
        }
        if( 0 < error.length() )
        {
            return error;
        }

        item.put( "coping_if", copingIfStatement );
        item.put( "coping_then", copingThenStatement );

        return error;
    }

}
