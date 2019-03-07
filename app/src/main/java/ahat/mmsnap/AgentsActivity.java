package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AgentsActivity extends AppCompatActivity
    implements android.view.View.OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agents );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.agents_section_logo ) );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //buttons events
        findViewById( R.id.agents_todays_btn ).setOnClickListener( this );
        findViewById( R.id.agents_weekly_btn ).setOnClickListener( this );
        findViewById( R.id.agents_attention_btn ).setOnClickListener( this );
        findViewById( R.id.agents_achievements_btn ).setOnClickListener( this );
    }

    @Override
    public void onClick( View view )
    {
        switch (view.getId()){
            case R.id.agents_todays_btn:
                startActivity( new Intent( this, TodaysPlansActivity.class ) );
                break;
            case R.id.agents_weekly_btn:
                break;
            case R.id.agents_attention_btn:
                break;
            case R.id.agents_achievements_btn:
                break;
            default:
                break;
        }
    }

}
