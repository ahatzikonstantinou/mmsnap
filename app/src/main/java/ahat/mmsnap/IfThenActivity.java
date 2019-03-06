package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class IfThenActivity extends AppCompatActivity implements View.OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_if_then );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo) );

        //buttons events
        Button fButton = ( Button ) findViewById( R.id.if_then_counterfactual_btn );
        fButton.setOnClickListener( this );
        Button aButton = ( Button ) findViewById( R.id.if_then_action_btn );
        aButton.setOnClickListener( this );
        Button cButton = ( Button ) findViewById( R.id.if_then_coping_btn );
        cButton.setOnClickListener( this );

    }

    @Override
    public void onClick( View view )
    {
        switch (view.getId()){
            case R.id.if_then_counterfactual_btn:
                 startActivity( new Intent( this, CounterfactualActivity.class) );
                break;
            case R.id.if_then_action_btn:
                startActivity( new Intent( this, ActionPlansActivity.class) );
                break;
            case R.id.if_then_coping_btn:
                startActivity( new Intent( this, CopingPlansActivity.class) );
                break;
            default:
                break;
        }
    }
}
